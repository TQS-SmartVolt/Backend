@UAT-Webaaaa
Feature: EV Driver Station Discovery
  To allow EV drivers to find available charging stations on a map

  Background:
    Given the EV driver is logged in with email "jane@example.com" and password "StrongPassword!"
    And the user is on page "/service/stations-map"

  Scenario: View all available charging stations on the map
    Then I should see the map displayed
    And I should see at least 1 charging station marker on the map
