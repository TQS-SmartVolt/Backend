@UAT-Web
Feature: Register Station

  Background: A user starts on the operator page
    Given I am on the operator page

  Scenario: Add a Station and check in the list
    When I add a station with name "Station Test" latitude 40.123 longitude -8.123
    Then I should see the station in the list with name "Station Test" status "Active"
