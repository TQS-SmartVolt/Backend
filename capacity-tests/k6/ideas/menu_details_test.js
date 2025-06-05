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
  const date = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0]; // tomorrow's date
  const res = http.get(`${BASE_URL}/menus/details?restaurantId=1&date=${date}&schedule=lunch`);

  check(res, {
    'GET /menus/details status is 200': (r) => r.status === 200,
  });

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
