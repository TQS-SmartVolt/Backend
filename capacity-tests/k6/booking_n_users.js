import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

const BASE_URL = 'http://localhost/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`;
const PASSWORD = 'password123!';
const numberOfDrivers = 5;

const driverEmails = new SharedArray('driverEmails', function () {
  const emails = [];
  for (let i = 1; i <= numberOfDrivers; i++) {
    emails.push(`evdriver${i}@example.com`);
  }
  return emails;
});

function getDriverTokenForVU() {
  const email = driverEmails[__VU - 1];
  const payload = JSON.stringify({ email: email, password: PASSWORD });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(LOGIN_URL, payload, params);

  check(res, { 'login status is 200': (r) => r.status === 200 });

  if (res.status !== 200) {
    console.error(`VU ${__VU}: Login failed for ${email}: ${res.status} - ${res.body}`);
    throw new Error(`Login failed for VU ${__VU}, aborting.`);
  }
  return res.json('token');
}

function getTomorrowDate() {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);

  const year = tomorrow.getFullYear();
  const month = (tomorrow.getMonth() + 1).toString().padStart(2, '0');
  const day = tomorrow.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}`;
}

export const options = {
  vus: numberOfDrivers,
  iterations: numberOfDrivers,

  thresholds: {
    'http_req_failed': ['rate<0.01'],
    'http_req_duration': ['p(95)<2000'],
  },
};

export default function () {
  const token = getDriverTokenForVU();
  const authHeaders = { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' };

  console.log(`VU ${__VU} (${driverEmails[__VU - 1]}): Logged in successfully.`);

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

  const randomSlot = availableSlots[Math.floor(Math.random() * availableSlots.length)];

  const bookingPayload = JSON.stringify({
    slotId: randomSlot.slotId,
    startTime: randomSlot.startTime,
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
