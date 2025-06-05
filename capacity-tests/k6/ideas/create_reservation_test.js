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
  const payload = JSON.stringify({
    date: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0], // tomorrow's date
    meals: [
      {
        dessertId: null,
        dishId: "Steak",
        drinkId: "Tea",
        fruitId: "Orange",
        soupId: "Chicken Soup"
      },
      {
        dessertId: "Apple Pie",
        dishId: "Mushroom Risotto",
        drinkId: "Orange Juice",
        fruitId: null,
        soupId: null
      }
    ],
    numberOfPeople: 2,
    restaurantId: 1,
    schedule: "dinner",
    studentEmail: "test.student1@moliceiro.pt"
  });

  const headers = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(`${BASE_URL}/reservations`, payload, headers);

  check(res, {
    'POST /reservations status is 201 or 200': (r) => r.status === 201 || r.status === 200,
  });

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
