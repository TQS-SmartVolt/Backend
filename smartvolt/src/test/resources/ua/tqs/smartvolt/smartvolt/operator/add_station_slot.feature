@UAT-Weba
Feature: Add Station Slot
  To allow operators to change stations status

  Background:
    Given the operator is logged in with email "johndoe@example.com" and password "StrongPassword!"
    And the website is available at page "/operator"

  Scenario: Add a Slot
    When the operator clicks on Add for station 0
    And the operator fills in the form with price 100 and charging speed "Slow"
    And the operator confirms the addition of the slot

    Then station 0 should have 4 slots
