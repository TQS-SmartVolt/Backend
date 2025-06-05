@UAT-Web
Feature: EV Driver View Profile
  As a driver, I want to view my profile.

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"

  Scenario: Verify profile
    And the user is on page "/service/account"
    Then I should see name "Jane Smith", email "test@example.com", totalEnergy 125 and totalMoney 175
