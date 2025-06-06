## Run light-house
lighthouse --view http://localhost

## Run k6
k6 run --out=experimental-prometheus-rw simple.js

## Test booking n users
k6 run --out=experimental-prometheus-rw booking_n_users.js

## Test to force a failure
k6 run --out=experimental-prometheus-rw stress_test_bookings.js
