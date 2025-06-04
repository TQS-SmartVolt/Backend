# @UAT-Web
Feature: Check Stats
  To allow operators to check stats.

  Background:
    Given the operator is logged in with email "johndoe@example.com" and password "StrongPassword!"
    And the website is available at page "/operator"

  Scenario: Check Stats
    Then the operator should see 20 totalSessions, 1.67 avgSession, 185 energyDelivered and 15.42 avgEnergy
