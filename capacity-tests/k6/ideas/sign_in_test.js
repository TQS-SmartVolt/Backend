import http from 'k6/http';
import { check, sleep } from 'k6';
import { commonThresholds, breakpointThresholds, generateScenarios } from '../common.js';

const BASE_URL = 'http://localhost:8080';
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
  const signInParams = {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  };
  const signInPayload = 'email=test.student1@moliceiro.pt';
  const signInRes = http.post(`${BASE_URL}/auth/sign-in`, signInPayload, signInParams);

  check(signInRes, {
    'Sign-in status is 200': (r) => r.status === 200,
  });

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
