import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// --- Constants ---
const BASE_URL = 'http://localhost/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`;
const PASSWORD = 'password123!';
const numberOfDrivers = 10; // Total number of EV drivers to simulate

// Pre-generate driver emails to avoid string concatenation per iteration
// This array will be created once and shared efficiently across all VUs.
const driverEmails = new SharedArray('driverEmails', function () {
  const emails = [];
  for (let i = 1; i <= numberOfDrivers; i++) { // From evdriver1 to evdriver10
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
  // This ensures each VU logs in as a unique driver (e.g., VU 1 -> evdriver1, VU 2 -> evdriver2).
  const email = driverEmails[__VU - 1];
  const payload = JSON.stringify({ email: email, password: PASSWORD });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(LOGIN_URL, payload, params);

  check(res, { 'login status is 200': (r) => r.status === 200 });

  // If login fails, throw an error to stop the VU, as subsequent requests will fail.
  if (res.status !== 200) {
    console.error(`VU ${__VU}: Login failed for ${email}: ${res.status} - ${res.body}`);
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
  tomorrow.setDate(tomorrow.getDate() + 1);

  const year = tomorrow.getFullYear();
  const month = (tomorrow.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-indexed
  const day = tomorrow.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}`;
}

// --- K6 Options (Concurrency Test for all 10 drivers) ---
export const options = {
  vus: 10,       // 10 virtual users, one for each driver, running concurrently
  iterations: 10, // Each of the 10 VUs will run the default function exactly once.
                   // If you remove 'iterations' and use 'duration', VUs will keep iterating.
  thresholds: {
    'http_req_failed': ['rate<0.01'], // http errors should be less than 1%
    'http_req_duration': ['p(95)<2000'], // 95% of requests should be below 2000ms
  },
};

// --- Main Virtual User (VU) Logic ---
// Each VU will execute this function based on the 'vus' and 'iterations' settings.
export default function () {
  // 1) Login current EV driver (dynamic based on VU ID)
  const token = getDriverTokenForVU();
  const authHeaders = { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };

  console.log(`VU ${__VU} (${driverEmails[__VU - 1]}): Logged in successfully.`);

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
    console.warn('No charging stations found. Skipping further steps in this iteration.');
    return;
  }

  // 3) Choose a random station and getChargingSlotsByStationId with chargingSpeed="Slow" and date for tomorrow
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

  check(bookingRes, {
    'POST /bookings/start-payment status is 200': (r) => r.status === 200,
    'bookingId is present in response': (r) => typeof r.json('bookingId') === 'number',
  });

  if (bookingRes.status !== 200) {
    console.error(`VU ${__VU}: Booking creation failed: ${bookingRes.status} - ${bookingRes.body} for slot ${randomSlot.slotId}`);
  }
}
