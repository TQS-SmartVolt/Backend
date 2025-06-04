# @UAT-Web
# Feature: EV Driver Confirm and View Payment Details
#   As a driver, I want to confirm my booking payment so that I can finalize the process.

#   Background:
#     Given the EV driver is logged in with email "test@example.com" and password "password123!"
#     And the user is on page "/service/stations-map"
#     # Pre-requisite steps to get to the booking page
#     And I should see the map displayed
#     And I expand the filter section
#     And I click the "Show Markers" button on the station map
#     And I click on the charging station marker at index 0
#     Then I should see a station details popup with title "Station 1" and address "Rua 1"
#     And I click on the View Details button
#     Then I should be in the page "/booking"
#     And I should see the booking page elements for "Station 1" and address "Rua 1"
#     And I select the date tomorrow
#     And I select the time slot "10:00"
#     And I click the "Confirm Booking" button on the booking page
#     Then I should see the booking confirmation dialog
#     And I click the "Confirm" button on the confirmation dialog
#     Then I should be on the payment details page
#     And I should see the payment details displayed
    
#     And I insert the card details with number "1111111111111111", expiration date "12/29", and CVV "123"
#     And I click the "Confirm Payment" button
#     Then I should see the payment confirmation dialog
      
#   Scenario: EV Driver Unlocks a Time Slot
#     When I click the "Confirm" button on the confirmation dialog
#     Then I should be on the unlock slot page
#     And I should see an entry for "10:00" in the slot list
#     And I click the "Unlock" button for the slot at index 0
#     Then I should see the unlock confirmation dialog
#     And I click the "Confirm" button on the confirmation dialog
#     Then I should be on the charging session page  