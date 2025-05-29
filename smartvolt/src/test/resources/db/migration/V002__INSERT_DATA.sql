-- Insert Operator data
-- Insert User data
INSERT INTO app_user (user_id, name, email, password) VALUES
(2, 'John Doe', 'johndoe@example.com', 'password123');

-- Insert Operator data
INSERT INTO station_operator (user_id) VALUES
(2);

-- Insert ChargingStation data
INSERT INTO charging_station (name, latitude, longitude, address, availability, operator_user_id) VALUES
('Station A', 37.7749, -122.4194, '123 Main St, San Francisco, CA', TRUE, 2),
('Station B', 34.0522, -118.2437, '456 Elm St, Los Angeles, CA', TRUE, 2),
('Station C', 40.7128, -74.0060, '789 Oak St, New York, NY', FALSE, 2);
