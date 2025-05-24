-- Insert Operator data
INSERT INTO operator (name, email, phone_number) VALUES
('SmartVolt', 'pedro@ua.pt', '123-456-7890');

-- Insert ChargingStation data
INSERT INTO charging_station (name, latitude, longitude, address, availability, operator_user_id) VALUES
('Station A', 37.7749, -122.4194, '123 Main St, San Francisco, CA', TRUE, 1),
('Station B', 34.0522, -118.2437, '456 Elm St, Los Angeles, CA', TRUE, 1),
('Station C', 40.7128, -74.0060, '789 Oak St, New York, NY', FALSE, 1);
