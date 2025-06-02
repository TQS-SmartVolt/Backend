@UAT-Webaaa
Feature: EV Driver Slot Availability
  To allow EV drivers to view available charging slots for a selected station

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"

  Scenario: User successfully navigates to the booking page and sees slot details
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the View Details button
    Then I should be in the page "/booking"
    And I should see the booking page elements for "Station 1" and address "Rua 1"

  Scenario: User sees "No available time slots for this speed." message when selecting an unavailable charging speed
    Then I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the View Details button
    Then I should be in the page "/booking"
    And I should filter the charging speed by "Fast"
    Then I should see a warning message
