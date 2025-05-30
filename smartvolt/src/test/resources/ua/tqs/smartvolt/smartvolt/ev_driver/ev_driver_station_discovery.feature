# src/test/resources/ua/tqs/smartvolt/smartvolt/features/ev_driver/ev_driver_station_discovery.feature
@UAT-Web
Feature: EV Driver Station Discovery

  As an EV driver,
  I want to be able to find available charging stations on a map,
  So that I can easily locate and navigate to a suitable charging point.

  @SV-19
  Scenario: View all available charging stations on the map
    Given the user is on page "/map"
    Then I should see the map displayed
    And I should see at least 1 charging station marker on the map

  @SV-19
  Scenario: Filter charging stations by speed
    Given the user is on page "/map"
    And I should see the map displayed
    When I click on the "Filter" button
    And I select "Fast" charging speed filter
    Then I should see 1 charging station marker on the map
    And the station details title should be "Station B"

  @SV-19
  Scenario: View details of a charging station
    Given the user is on page "/map"
    And I should see the map displayed
    And there is at least 1 charging station marker on the map
    When I click on the first charging station marker
    Then I should see the station details popup displayed
    And the station details title should be "Station A"
    And the station details address should be "123 Main St, San Francisco, CA"
    And the station details view details button should be displayed
