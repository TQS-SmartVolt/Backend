import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// --- Constants ---
const BASE_URL = 'http://localhost/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`;
const PASSWORD = 'password123!';
const numberOfDrivers = 1000; // Total number of EV drivers. Match DataLoaderCapacity

// Pre-generate driver emails to avoid string concatenation per iteration
// This array will be created once and shared efficiently across all VUs.
const driverEmails = new SharedArray('driverEmails', function () {
  const emails = [];
  for (let i = 1; i <= numberOfDrivers; i++) {
    emails.push(`evdriver${i}@example.com`);
  }
  return emails;
});

// --- Helper Functions ---

/**
 * Logs in the EV driver associated with the current VU and returns their authentication token.
 * Includes a check to ensure login is successful.
 * @returns {string} The authentication token.
 */
function getDriverTokenForVU() {
  // __VU is 1-indexed, so we use __VU - 1 to get the correct index from the driverEmails array.
  const email = driverEmails[__VU - 1];
  const payload = JSON.stringify({ email: email, password: PASSWORD });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(LOGIN_URL, payload, params);

  check(res, { 'login status is 200': (r) => r.status === 200 });

  // If login fails, throw an error to stop the VU, as subsequent requests will fail.
  if (res.status !== 200) {
    console.error(`VU ${__VU} (${email}): Login failed: ${res.status} - ${res.body}`);
    throw new Error(`Login failed for VU ${__VU}, aborting.`);
  }
  return res.json('token');
}

/**
 * Generates tomorrow's date in YYYY-MM-DD format.
 * @returns {string} Tomorrow's date string.
 */
function getTomorrowDate() {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 2);

  const year = tomorrow.getFullYear();
  const month = (tomorrow.getMonth() + 1).toString().padStart(2, '0');
  const day = tomorrow.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}`;
}

// --- K6 Options (Stress Test Configuration) ---
export const options = {
  // Use a 'ramping-vus' executor to gradually increase the load,
  // which is ideal for finding the breaking point.
  scenarios: {
    stress: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 5 },  // Ramp up to 5 VUs over 30 s (initial load)
        { duration: '30s', target: 10 },  // Ramp up to 10 VUs over 1 minute
        { duration: '30s', target: 0 },  // Ramp down to 0 VUs over 1 minute
      ],
      gracefulStop: '30s', // Wait up to 30s for iterations to finish during ramp-down
      // The `exec` property defaults to the `default` function, so no need to specify it here.
    },
  },

};

// --- Main Virtual User (VU) Logic ---
// Each VU will execute this function repeatedly as per the 'scenarios' definition.
export default function () {
  // 1) Login current EV driver (dynamic based on VU ID)
  const token = getDriverTokenForVU();
  const authHeaders = { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };

  // Logging per VU might be excessive for 1000 VUs; consider removing for actual large tests.
  // console.log(`VU ${__VU} (${driverEmails[__VU - 1]}): Logged in successfully.`);

  // 2) getChargingStationsByChargingSpeed with chargingSpeeds=["Slow", "Medium", "Fast"]
  const stationsUrl = `${BASE_URL}/stations/map?chargingSpeeds=Slow,Medium,Fast`;
  const stationsRes = http.get(stationsUrl, { headers: authHeaders });

  check(stationsRes, {
    'GET /stations/map status is 200': (r) => r.status === 200,
    'stations response is an object with a stations array': (r) => r.json() && typeof r.json() === 'object' && Array.isArray(r.json().stations),
    'stations array is not empty': (r) => r.json().stations && r.json().stations.length > 0,
  });

  const stations = stationsRes.json('stations');
  if (!stations || stations.length === 0) {
    console.warn(`VU ${__VU}: No charging stations found. Skipping further steps.`);
    sleep(1); // Add a small sleep to prevent rapid failing VUs
    return;
  }

  // 3) Choose a random station and getChargingSlotsByStationId with chargingSpeed="Slow" and date for tomorrow
  // The 'Slow' charging speed is specifically chosen due to limited slots (2 per station) for contention.
  const randomStation = stations[Math.floor(Math.random() * stations.length)];
  const tomorrowDate = getTomorrowDate();
  const slotsUrl = `${BASE_URL}/stations/${randomStation.stationId}/slots?chargingSpeed=Slow&date=${tomorrowDate}`;
  const slotsRes = http.get(slotsUrl, { headers: authHeaders });

  check(slotsRes, {
    'GET /stations/{id}/slots status is 200': (r) => r.status === 200,
    'slots response is an object with availableSlotMapping array': (r) => r.json() && typeof r.json() === 'object' && Array.isArray(r.json().availableSlotMapping),
    'available slots array is not empty': (r) => r.json().availableSlotMapping && r.json().availableSlotMapping.length > 0,
  });

  const availableSlots = slotsRes.json('availableSlotMapping');
  if (!availableSlots || availableSlots.length === 0) {
    console.warn(`VU ${__VU}: No 'Slow' slots available for station ${randomStation.stationId} on ${tomorrowDate}. Skipping booking.`);
    sleep(1); // Add a small sleep to prevent rapid failing VUs
    return;
  }

  // 4) Choose a random available slot
  const randomSlot = availableSlots[Math.floor(Math.random() * availableSlots.length)];

  // 5) createBooking with the chosen slot
  const bookingPayload = JSON.stringify({
    slotId: randomSlot.slotId,
    startTime: randomSlot.startTime, // Use the exact startTime provided by the API for the slot
  });
  const createBookingUrl = `${BASE_URL}/bookings/start-payment`;
  const bookingRes = http.post(createBookingUrl, bookingPayload, { headers: authHeaders });

  // IMPORTANT: Modified check for booking status to account for 409 Conflicts
  check(bookingRes, {
    'POST /bookings/start-payment status is 200 or 409': (r) => r.status === 200 || r.status === 409,
    'booking successful (200)': (r) => r.status === 200, // Custom tag for successful bookings
    'booking conflict (409)': (r) => r.status === 409,   // Custom tag for conflict bookings
    'bookingId is present on 200 response': (r) => r.status === 200 ? typeof r.json('bookingId') === 'number' : true,
  });

  if (bookingRes.status !== 200 && bookingRes.status !== 409) {
    console.error(`VU ${__VU}: Booking creation failed with unexpected status: ${bookingRes.status} - ${bookingRes.body} for slot ${randomSlot.slotId}`);
  } else if (bookingRes.status === 409) {
    console.warn(`VU ${__VU}: Booking conflict for slot ${randomSlot.slotId} - Status: 409 Conflict.`);
  }

  // Sleep time to simulate user "think time" and prevent hammering the server too aggressively
  // A small sleep helps in observing behavior under sustained load, not just immediate burst.
  sleep(1);
}
