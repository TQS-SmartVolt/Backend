@UAT-Web
Feature: Register Station
  To allow operators to register new stations.

  Background:
    Given the operator is logged in with email "johndoe@example.com" and password "password123"
    And the website is available at page "/operator"

  Scenario: Add a new Station
    When the operator clicks on "Add Station"
    And the modal should "appear"
    And the operator fills in the form with name "Station Test", address "Third Street", latitude 40.123, and longitude -8.123
    And the operator confirms the registration

    Then the modal should "close"
    And 4 stations should have been found
    And station 4 should have the name "Station Test", status "Active", address "Third Street", and 0 slots

 # TODO: change "address" to the expected address once the geocoding service is implemented!
