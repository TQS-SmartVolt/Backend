import http from 'k6/http';
import { check, sleep } from 'k6';
import { commonThresholds, breakpointThresholds, generateScenarios } from './common.js';

const BASE_URL = 'http://localhost/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`; // Adjust if your login endpoint is different
const EMAIL = 'evdriver1@example.com';
const PASSWORD = 'password123!';

const selectedProfile = __ENV.TEST_TYPE || 'smoke';
const scenarios = generateScenarios(selectedProfile);

export const options = {
  scenarios,
  thresholds: {
    ...commonThresholds,
    ...(selectedProfile === 'breakpoint' ? breakpointThresholds : {}),
  },
};

function getDriverToken() {
  const payload = JSON.stringify({ email: EMAIL, password: PASSWORD });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(LOGIN_URL, payload, params);
  check(res, { 'login status is 200': (r) => r.status === 200 });
  return res.json('token');
}

export function smokeTest() {
  const token = getDriverToken();
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };
  const res = http.get(`${BASE_URL}/bookings/current-bookings`, params);

  check(res, {
    'GET /current-bookings status is 200': (r) => r.status === 200,
    'response is an array': (r) => Array.isArray(r.json()),
  });

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
