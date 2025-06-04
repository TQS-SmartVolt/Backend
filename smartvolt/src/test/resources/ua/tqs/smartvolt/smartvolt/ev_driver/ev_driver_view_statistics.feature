@UAT-Web
Feature: EV Driver View Personal Charging Statistics
  As a driver, I want to view statistics about my energy consumption and spending over time, so that I can track my usage and manage expenses.

  Background:
    Given the EV driver is logged in with email "test@example.com" and password "password123!"
    And the user is on page "/service/stations-map"

  Scenario: Verify statistics dashboard components are displayed
    And the user is on page "/service/account"
    Then I should be on the statistics page
    And I should see the "Energy Consumption (kWh)" chart description
    And I should see the "Energy Consumption (kWh)" chart container
    And I should see the "Spending (€)" chart description
    And I should see the "Spending (€)" chart container
