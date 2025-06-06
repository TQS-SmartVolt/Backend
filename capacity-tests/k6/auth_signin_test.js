// auth_signin_test.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { commonThresholds, breakpointThresholds, generateScenarios } from './common.js';

const BASE_URL = 'http://localhost/api/v1';
const LOGIN_URL = `${BASE_URL}/auth/sign-in`;

const EXISTING_EV_DRIVER_EMAIL = 'evdriver1@example.com';
const EXISTING_EV_DRIVER_PASSWORD = 'password123!';

const selectedProfile = __ENV.TEST_TYPE || 'smoke';
const scenarios = generateScenarios(selectedProfile);

export const options = {
  scenarios,
  thresholds: {
    ...commonThresholds,
    ...(selectedProfile === 'breakpoint' ? breakpointThresholds : {}),
  },
};

export function smokeTest() {
  const payload = JSON.stringify({
    email: EXISTING_EV_DRIVER_EMAIL,
    password: EXISTING_EV_DRIVER_PASSWORD
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(LOGIN_URL, payload, params);

  check(res, {
    'Sign-in status is 200': (r) => r.status === 200,
    'Response contains a token': (r) => r.json() && typeof r.json().token === 'string' && r.json().token.length > 0,
  });

  if (res.status === 200) {
    const token = res.json('token');
  } else {
    console.error(`Sign-in failed for user ${EXISTING_EV_DRIVER_EMAIL}: Status ${res.status}, Body: ${res.body}`);
  }

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
