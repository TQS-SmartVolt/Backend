-- Insert Operator data
-- Insert User data


-- For Testing Purposes:
-- token: eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzQ4NTU1NDUxLCJleHAiOjE3NDg2NDE4NTF9.ZlB2i1XF3tgmqOh7g1PWe20IWd6SaBhOd3q41_JsdWL2uBv8QZf8UQyvibvAS_jHruzzqwKrXC1rxenHJ2FNgHU4n69QJRxQmYhX50o6HUjevBDrmPXo-WvrZfbLv_UOh1Reh0E5g4Ordft9x2vBmXVHI7Rk9Gt1OfmQynELlEeZkK-lrc04BEdxPIe1e0EFl5kgJ7uTjXP7KEMlB2cK0onhDhtZ4D1khuM24p8ghetcytQftQq6HY3vPjQ7Oo4vcD8DQPUEA94Xg2qMotlo39ZwP6LvM4ngOU-s_Q1H2Fv2PNYwRJPYuz_umsdjkCyMeyKRm25ypwG9Fc59dRrDLA
-- password: password123
INSERT INTO app_user (user_id, name, email, password, roles) VALUES
(2, 'Test Operator', 'test@example.com', '$2a$10$kdu.QsBQ0YiyLBy8xkLjluLeCtMei1SO026h6jJcwygCtj.84PmG2', '{ROLE_STATION_OPERATOR}');
INSERT INTO station_operator (user_id) VALUES
(2);
INSERT INTO charging_station (name, latitude, longitude, address, availability, operator_id) VALUES
('Station 1', 12.34, 56.78, 'Address 1', TRUE, 2);
