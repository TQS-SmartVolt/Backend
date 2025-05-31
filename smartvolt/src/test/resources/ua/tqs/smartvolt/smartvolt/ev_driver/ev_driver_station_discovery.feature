@UAT-Web
Feature: EV Driver Station Discovery
  To allow EV drivers to find available charging stations on a map

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"

  Scenario: View all available charging stations on the map
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button
    Then I should see exactly 2 charging station markers on the map

  Scenario: Verify charging station marker details
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the charging station marker at index 1
    Then I should see a station details popup with title "Station 2" and address "Rua 2"

  Scenario: The map supports zoom and pan interactions to explore different areas
    Then I should see the map displayed
    And I zoom in on the map
    Then the map view should have changed its zoom level
    And I zoom out on the map
    Then the map view should have changed its zoom level
    And I pan the map by 50 pixels horizontally and 50 pixels vertically
    Then the map position should have changed

  Scenario: The user can apply a filter by charging speed.
    Then I should see the map displayed
    And I expand the filter section
    And I click on Select All Filter to remove all the filters
    And I click on Slow Filter
    And I click the "Show Markers" button
    Then I should see exactly 1 charging station markers on the map

  Scenario: Navigate to booking page from station details
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the View Details button
    Then I should be in the page "/booking"
