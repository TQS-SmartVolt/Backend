# @UAT-Web
Feature: Change Station Status
  To allow operators to change stations status

  Background:
    Given the operator is logged in with email "johndoe@example.com" and password "StrongPassword!"
    And the website is available at page "/operator"

  Scenario: Deactivate a station
    When the operator clicks on "Deactivate" on station 1
    And the operator fills in the form with reason "Maintenance"
    And the operator confirms the "deactivation" action

    Then station 2 should have the status "Inactive"
