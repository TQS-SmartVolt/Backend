import http from 'k6/http';
import { check, sleep } from 'k6';
import { commonThresholds, breakpointThresholds, generateScenarios } from './common.js';

const BASE_URL = 'http://localhost/api/v1';

const selectedProfile = __ENV.TEST_TYPE || 'smoke';
const scenarios = generateScenarios(selectedProfile);

export const options = {
  scenarios,
  thresholds: {
    ...commonThresholds,
    ...(selectedProfile === 'breakpoint' ? breakpointThresholds : {}),
  },
};

function generateUniqueUser() {
  const vuId = __VU;
  const iteration = __ITER;
  const timestamp = Date.now();

  const name = `TestUser_${vuId}_${iteration}_${timestamp}`;
  const email = `testuser_${vuId}_${iteration}_${timestamp}@example.com`;
  const password = 'Password123!';
  return { name, email, password };
}

export function smokeTest() {
  const newUser = generateUniqueUser();

  const payload = JSON.stringify({
    name: newUser.name,
    email: newUser.email,
    password: newUser.password,
    role: 'ROLE_EV_DRIVER'
  });

  console.log(`Creating user: ${newUser.name}, Email: ${newUser.email}`);

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(`${BASE_URL}/auth/sign-up`, payload, params);

  check(res, {
    'Sign-up status is 200 (or 201 Created)': (r) => r.status === 200 || r.status === 201,
    'Response body contains userId': (r) => r.json() && typeof r.json().userId === 'number',
    'Response body contains name': (r) => r.json('name') === newUser.name,
    'Response body contains email': (r) => r.json('email') === newUser.email,
  });

  if (res.status !== 200 && res.status !== 201) {
    console.error(`Sign-up failed for user ${newUser.email}: Status ${res.status}, Body: ${res.body}`);
  }

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
