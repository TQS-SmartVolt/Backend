import http from 'k6/http';
import { check, sleep } from 'k6';
import { commonThresholds, breakpointThresholds, generateScenarios } from './common.js';

const BASE_URL = 'http://localhost:8080/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`;
const GET_STATIONS_MAP_URL = `${BASE_URL}/stations/map`;

const DRIVERS_COUNT = 5; 
const COMMON_PASSWORD = 'password123!';

const selectedProfile = __ENV.TEST_TYPE || 'smoke';
const scenarios = generateScenarios(selectedProfile);

export const options = {
  scenarios,
  thresholds: {
    ...commonThresholds,
    ...(selectedProfile === 'breakpoint' ? breakpointThresholds : {}),
  },
};

const tokens = {};

export function setup() {
  console.log('Authenticating a few EV Drivers in setup phase...');
  for (let i = 1; i <= DRIVERS_COUNT; i++) {
    const email = `evdriver${i}@example.com`;
    const payload = JSON.stringify({ email: email, password: COMMON_PASSWORD });
    const params = { headers: { 'Content-Type': 'application/json' } };
    const res = http.post(LOGIN_URL, payload, params);

    check(res, { [`setup: driver ${i} login status is 200`]: (r) => r.status === 200 });
    if (res.status === 200) {
      tokens[email] = res.json('token');
      console.log(`Authenticated ${email}`);
    } else {
      console.error(`Failed to authenticate ${email}: Status ${res.status}, Body: ${res.body}`);
    }
  }
  return { tokens: tokens };
}

export function smokeTest(data) {
  const vuId = __VU;
  const driverIndex = (vuId % DRIVERS_COUNT) + 1; // cycle through available drivers
  const email = `evdriver${driverIndex}@example.com`;
  const token = data.tokens[email];

  if (!token) {
    console.error(`Token not found for ${email}. Skipping request.`);
    return;
  }

  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

	const chargingSpeeds = ['Slow', 'Medium', 'Fast'];
  const randomChargingSpeed = chargingSpeeds[Math.floor(Math.random() * chargingSpeeds.length)];

  const res = http.get(`${GET_STATIONS_MAP_URL}?chargingSpeeds=${randomChargingSpeed}`, params);

  check(res, {
    'GET /stations/map status is 200': (r) => r.status === 200,
    'Response is an object': (r) => typeof r.json() === 'object',
    'Response contains content': (r) => r.json().stations && Array.isArray(r.json().stations),
  });

  if (res.status !== 200) {
    console.error(`GET /stations/map failed for ${email}: Status ${res.status}, Body: ${res.body}`);
  }

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;