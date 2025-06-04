# @UAT-Web
Feature: EV Driver Station Discovery
  To allow EV drivers to find available charging stations on a map

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"

  Scenario: View all available charging stations on the map
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    Then I should see exactly 2 charging station markers on the map

  Scenario: Verify charging station marker details
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the charging station marker at index 1
    Then I should see a station details popup with title "Station 2" and address "Rua 2"

  Scenario: The user can apply a filter by charging speed.
    Then I should see the map displayed
    And I expand the filter section
    And I click on Select All Filter to remove all the filters
    And I click on Slow Filter
    And I click the "Show Markers" button on the station map
    Then I should see exactly 1 charging station markers on the map

  Scenario: Navigate to booking page from station details
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the View Details button
    Then I should be in the page "/booking"
