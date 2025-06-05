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
  // sign in
  const signInParams = {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  };
  const signInPayload = 'email=test.student1@moliceiro.pt';
  const signInRes = http.post(`${BASE_URL}/auth/sign-in`, signInPayload, signInParams);
  check(signInRes, { 'Sign-in status is 200': (r) => r.status === 200 });

  // get restaurants
  const restaurantsRes = http.get(`${BASE_URL}/restaurants`);
  check(restaurantsRes, { 'GET /restaurants is 200': (r) => r.status === 200 });

  // get menu by restaurant id
  const menuRes = http.get(`${BASE_URL}/menus/1`);
  check(menuRes, { 'GET /menus/1 is 200': (r) => r.status === 200 });

  // get menu details for specific date and schedule
  const date = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0]; // tomorrow's date
  const menuDetailsRes = http.get(`${BASE_URL}/menus/details?restaurantId=1&date=${date}&schedule=dinner`);
  check(menuDetailsRes, { 'GET /menus/details is 200': (r) => r.status === 200 });

  // create reservation
  const reservationPayload = JSON.stringify({
    date: date,
    meals: [
      {
        dessertId: "Apple Pie",
        dishId: "Steak",
        drinkId: "Coffee",
        fruitId: "Pineapple",
        soupId: "Chicken Soup"
      },
      {
        dessertId: null,
        dishId: "Fried Cod",
        drinkId: null,
        fruitId: "Pineapple",
        soupId: null
      }
    ],
    numberOfPeople: 2,
    restaurantId: 1,
    schedule: "dinner",
    studentEmail: "test.student1@moliceiro.pt"
  });

  const reservationHeaders = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const reservationRes = http.post(`${BASE_URL}/reservations`, reservationPayload, reservationHeaders);
  check(reservationRes, {
    'POST /reservations is 201 or 200 or 400': (r) => r.status === 201 || r.status === 200,
  });

  // get reservations by email
  const reservationsByEmailRes = http.get(`${BASE_URL}/reservations/email/test.student1@moliceiro.pt`);
  check(reservationsByEmailRes, {
    'GET /reservations/email is 200': (r) => r.status === 200,
  });

  sleep(1);
}

export const loadTest = smokeTest;
export const stressTest = smokeTest;
export const soakTest = smokeTest;
export const spikeTest = smokeTest;
export const breakpointTest = smokeTest;
