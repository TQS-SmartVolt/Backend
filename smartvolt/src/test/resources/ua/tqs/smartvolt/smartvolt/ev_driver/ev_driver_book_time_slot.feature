@UAT-Webaa
Feature: EV Driver Book a Time Slot
  To allow EV drivers to book a specific charging slot

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"
    # Pre-requisite steps to get to the booking page
    And I should see the map displayed
    And I expand the filter section
    And I click the "Show Markers" button on the station map
    And I click on the charging station marker at index 0
    Then I should see a station details popup with title "Station 1" and address "Rua 1"
    And I click on the View Details button
    Then I should be in the page "/booking"
    And I should see the booking page elements for "Station 1" and address "Rua 1"

  Scenario: System displays a warning if date or time slot not selected before proceeding
    When I select the date tomorrow
    And I click the "Confirm Booking" button on the booking page
    Then I should see a warning message "Please select both a date and a time slot."
