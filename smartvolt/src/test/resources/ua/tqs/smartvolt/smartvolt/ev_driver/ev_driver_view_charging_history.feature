# @UAT-Web
Feature: EV Driver View Charging History
  As a driver, I want to view my charging history so that I can review my charging past sessions.

  Background:
    Given the EV driver is logged in with email "newtest@example.com" and password "passwordXPTO!"
    And the user is on page "/service/stations-map"

  Scenario: View charging history with existing sessions
    And the user is on page "/service/history"
    Then I should be on the charging history page
    And I should see the charging history table displayed
    And I should see 1 charging sessions in the history
    And the 1 charging session entry should display:
      | Date                | Station        | Charging Speed | Charging Power (kW) | Energy Delivered (kWh) | Price per kWh | Cost  |
      | tomorrow, 09:00     | Station 1      | Slow           | 10 kW            | 5 kWh               | 0.20€         | 1.00€ |

  Scenario: View charging history with no sessions
    Given the EV driver is logged in with email "nohistory@example.com" and password "passwordXPTO!"
    And the user is on page "/service/stations-map"
    And the user is on page "/service/history"
    Then I should be on the charging history page
    And I should see a message indicating no charging history found
