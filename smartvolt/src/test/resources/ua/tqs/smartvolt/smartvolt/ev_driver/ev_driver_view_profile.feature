@UAT-Web
Feature: EV Driver View Profile
  As a driver, I want to view my profile.

  Background:
    Given the EV driver is logged in with email "driver5.3@example.com" and password "password5.3!"
    And the user is on page "/service/stations-map"

  Scenario: Verify profile
    And the user is on page "/service/account"
    Then I should see name "Maria Silva", email "driver5.3@example.com", totalEnergy 5 and totalMoney 1
