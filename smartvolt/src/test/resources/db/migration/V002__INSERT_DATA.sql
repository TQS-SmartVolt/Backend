-- Flyway migration script
-- Version: 2
-- Description: Insert initial data for SmartVolt application

-- For Testing Purposes:
-- token: eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzQ4NTU1NDUxLCJleHAiOjE3NDg2NDE4NTF9.ZlB2i1XF3tgmqOh7g1PWe20IWd6SaBhOd3q41_JsdWL2uBv8QZf8UQyvibvAS_jHruzzqwKrXC1rxenHJ2FNgHU4n69QJRxQmYhX50o6HUjevBDrmPXo-WvrZfbLv_UOh1Reh0E5g4Ordft9x2vBmXVHI7Rk9Gt1OfmQynELlEeZkK-lrc04BEdxPIe1e0EFl5kgJ7uTjXP7KEMlB2cK0onhDhtZ4D1khuM24p8ghetcytQftQq6HY3vPjQ7Oo4vcD8DQPUEA94Xg2qMotlo39ZwP6LvM4ngOU-s_Q1H2Fv2PNYwRJPYuz_umsdjkCyMeyKRm25ypwG9Fc59dRrDLA
-- password: password123
INSERT INTO app_user (user_id, name, email, password, roles) VALUES
(2, 'Test Operator', 'test@example.com', '$2a$10$kdu.QsBQ0YiyLBy8xkLjluLeCtMei1SO026h6jJcwygCtj.84PmG2', '{ROLE_STATION_OPERATOR}');
INSERT INTO station_operator (user_id) VALUES
(2);
INSERT INTO charging_station (name, latitude, longitude, address, availability, operator_id) VALUES
('Station 1', 12.34, 56.78, 'Address 1', TRUE, 2);

-- New EV Driver User
-- token: eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzQ4NTU1NDUxLCJleHAiOjE3NDg2NDE4NTF9.ZlB2i1XF3tgmqOh7g1PWe20IWd6SaBhOd3q41_JsdWL2uBv8QZf8UQyvibvAS_jHruzzqwKrXC1rxenHJ2FNgHU4n69QJRxQmYhX50o6HUjevBDrmPXo-WvrZfbLv_UOh1Reh0E5g4Ordft9x2vBmXVHI7Rk9Gt1OfmQynELlEeZkK-lrc04BEdxPIe1e0EFl5kgJ7uTjXP7KEMlB2cK0onhDhtZ4D1khuM24p8ghetcytQftQq6HY3vPjQ7Oo4vcD8DQPUEA94Xg2qMotlo39ZwP6LvM4ngOU-s_Q1H2Fv2PNYwRJPYuz_umsdjkCyMeyKRm25ypwG9Fc59dRrDLA
-- password: password123
INSERT INTO app_user (user_id, name, email, password, roles) VALUES
(3, 'Test Driver', 'evdriver@example.com', '$2a$10$kdu.QsBQ0YiyLBy8xkLjluLeCtMei1SO026h6jJcwygCtj.84PmG2', '{ROLE_EV_DRIVER}');
INSERT INTO ev_driver (user_id) VALUES
(3);

-- Existing Charging Station (Station 1) linked to Operator 2
INSERT INTO charging_station (station_id, name, latitude, longitude, address, availability, operator_id) VALUES
(101, 'Station 1', 12.34, 56.78, 'Address 1', TRUE, 2);

-- New Charging Stations linked to Operator 2
INSERT INTO charging_station (station_id, name, latitude, longitude, address, availability, operator_id) VALUES
(102, 'Station Slow', 40.7128, -74.0060, '123 Slow St', TRUE, 2),
(103, 'Station Medium', 34.0522, -118.2437, '456 Medium Rd', TRUE, 2),
(104, 'Station Fast', 51.5074, 0.1278, '789 Fast Blvd', TRUE, 2),
(105, 'Station Mixed', 48.8566, 2.3522, '101 Mixed Ave', TRUE, 2);

-- Charging Slots for newly added stations
-- Station Slow (102) - only Slow slots
INSERT INTO charging_slot (slot_id, is_locked, price_perkwh, power, charging_speed, station_id) VALUES
(201, TRUE, 0.15, 10, 'Slow', 102),
(202, TRUE, 0.15, 10, 'Slow', 102),
(207, TRUE, 0.15, 10, 'Slow', 102),
(208, TRUE, 0.15, 10, 'Slow', 102),
(209, TRUE, 0.15, 10, 'Slow', 102);

-- Station Medium (103) - only Medium slots
INSERT INTO charging_slot (slot_id, is_locked, price_perkwh, power, charging_speed, station_id) VALUES
(203, TRUE, 0.25, 20, 'Medium', 103);

-- Station Fast (104) - only Fast slots
INSERT INTO charging_slot (slot_id, is_locked, price_perkwh, power, charging_speed, station_id) VALUES
(204, TRUE, 0.40, 30, 'Fast', 104);

-- Station Mixed (105) - Slow and Medium slots
INSERT INTO charging_slot (slot_id, is_locked, price_perkwh, power, charging_speed, station_id) VALUES
(205, TRUE, 0.18, 10, 'Slow', 105),
(206, TRUE, 0.28, 20, 'Medium', 105);

INSERT INTO booking (booking_id, driver_id, slot_id, start_time, status, cost) VALUES
(301, 3, 207, date_trunc('hour', NOW()) + INTERVAL '30 minutes' * floor(date_part('minute', NOW()) / 30.0), 'paid', 1.5), -- Booking for Test Driver on Station Slow
(302, 3, 208, date_trunc('hour', NOW()) - INTERVAL '2 hour', 'not_used', 2.5),
(303, 3, 209, date_trunc('hour', NOW()) + INTERVAL '2 hour', 'paid', 2.5); -- Booking for Test Driver on Station Medium
