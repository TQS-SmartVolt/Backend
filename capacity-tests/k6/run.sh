#!/bin/bash

# K6 output backend
OUTPUT="--out=experimental-prometheus-rw"

# Directory containing test files
TEST_DIR="./"

# Test files
TEST_FILES=(
  "create_reservation_test.js"
  "flow_create_reservation_test.js"
  "get_reservation_by_id_test.js"
  "menu_by_restaurant_id_test.js"
  "menu_details_test.js"
  "restaurants-test.js"
  "sign_in_test.js"
)

# Test profiles
PROFILES=("smoke" "load" "stress" "soak" "spike" "breakpoint")

echo "Running K6 performance tests..."

for file in "${TEST_FILES[@]}"; do
  if [[ "$file" == "create_reservation_test.js" || "$file" == "flow_create_reservation_test.js" ]]; then
    echo "▶ Running smoke test on $file"
    k6 run $OUTPUT -e TEST_TYPE=smoke "$TEST_DIR/$file"
  else
    for profile in "${PROFILES[@]}"; do
      echo "▶ Running $profile test on $file"
      k6 run $OUTPUT -e TEST_TYPE=$profile "$TEST_DIR/$file"
    done
  fi
done

echo "✅ All K6 tests completed."

# k6 run
