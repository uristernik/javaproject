-- Connect to the 'mydb' database (optional, but good practice for clarity)
\c mydb;

-- Create USERS table
CREATE TABLE IF NOT EXISTS USERS (
    userID SERIAL PRIMARY KEY,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT NOT NULL,
    hashedPassword TEXT,
    type INTEGER
);

-- Create ORDERS table
CREATE TABLE IF NOT EXISTS ORDERS (
    orderID SERIAL PRIMARY KEY,
    userID INTEGER REFERENCES USERS(userID),
    userOrderId INTEGER,
    deliveryAddress TEXT,
    totalPrice DECIMAL(10,2)
);

-- Create INVENTORY table
CREATE TABLE IF NOT EXISTS INVENTORY (
    productID SERIAL PRIMARY KEY,
    description TEXT,
    stockKG DECIMAL(10,2),
    pricePerKG INTEGER
);

-- Create ORDER_ITEMS table
CREATE TABLE IF NOT EXISTS ORDER_ITEMS (
    serialID SERIAL PRIMARY KEY,
    orderID INTEGER NOT NULL REFERENCES ORDERS(orderID),
    productID INTEGER NOT NULL REFERENCES INVENTORY(productID),
    quantityKG DECIMAL(10,2),
    pricePerKG INTEGER
);

-- Create REVIEWS table
CREATE TABLE IF NOT EXISTS REVIEWS (
    productID INTEGER REFERENCES INVENTORY(productID) PRIMARY KEY,
    numberOfRatings INTEGER,
    sumOfReviews INTEGER
);

-- Insert default user with ID 1000 to avoid conflicts with auto-increment
INSERT INTO USERS (userID, firstName, lastName, email, phone, hashedPassword, type)
VALUES (1000, 'Admin', 'User', 'admin@example.com', '1234567890', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 2)
ON CONFLICT (userID) DO NOTHING;

-- Insert Israeli users with IDs 1001-1010
INSERT INTO USERS (userID, firstName, lastName, email, phone, hashedPassword, type) VALUES
    (1001, 'Moshe', 'Cohen', 'moshe.cohen@example.com', '0501234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1002, 'Sarah', 'Levy', 'sarah.levy@example.com', '0521234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1003, 'David', 'Mizrahi', 'david.mizrahi@example.com', '0531234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1004, 'Yael', 'Goldberg', 'yael.goldberg@example.com', '0541234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1005, 'Avi', 'Peretz', 'avi.peretz@example.com', '0551234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1006, 'Tamar', 'Katz', 'tamar.katz@example.com', '0561234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1007, 'Noam', 'Avraham', 'noam.avraham@example.com', '0571234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1008, 'Michal', 'Friedman', 'michal.friedman@example.com', '0581234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1009, 'Yosef', 'Shapira', 'yosef.shapira@example.com', '0591234567', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1),
    (1010, 'Shira', 'Rosenberg', 'shira.rosenberg@example.com', '0501234568', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1)
ON CONFLICT (userID) DO NOTHING;

-- Reset the sequence to start after our manually inserted users
SELECT setval('users_userid_seq', 1010, true);

-- Make sure your INSERT statements are correct and being executed
INSERT INTO INVENTORY (description, stockKG, pricePerKG) VALUES
    ('Organic Apples', 200, 3),
    ('Fresh Bananas', 120, 2),
    ('Carrots', 90, 2),
    ('Tomatoes', 70, 4),
    ('Potatoes', 250, 1),
    ('Oranges', 140, 3),
    ('Broccoli', 45, 5),
    ('Spinach', 35, 6),
    ('Sweet Potatoes', 100, 3),
    ('Bell Peppers', 60, 4);

-- Create Orders for Moshe Cohen (userID = 1001)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (1, 1001, 1, 'Herzl 10, Tel Aviv', 35.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Moshe's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (1, 1, 5.0, 3), -- 5kg of Organic Apples
    (1, 3, 2.5, 2), -- 2.5kg of Carrots
    (1, 5, 10.0, 1); -- 10kg of Potatoes

-- Order 2
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (2, 1001, 2, 'Herzl 10, Tel Aviv', 42.50)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Moshe's second order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (2, 2, 3.0, 2), -- 3kg of Fresh Bananas
    (2, 4, 2.5, 4), -- 2.5kg of Tomatoes
    (2, 6, 5.0, 3), -- 5kg of Oranges
    (2, 8, 1.5, 6); -- 1.5kg of Spinach

-- Create Orders for Sarah Levy (userID = 1002)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (3, 1002, 1, 'Ben Yehuda 25, Jerusalem', 28.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Sarah's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (3, 1, 2.0, 3), -- 2kg of Organic Apples
    (3, 6, 4.0, 3), -- 4kg of Oranges
    (3, 7, 2.0, 5); -- 2kg of Broccoli

-- Create Orders for David Mizrahi (userID = 1003)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (4, 1003, 1, 'Rothschild 50, Tel Aviv', 45.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for David's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (4, 4, 3.0, 4), -- 3kg of Tomatoes
    (4, 8, 2.5, 6), -- 2.5kg of Spinach
    (4, 10, 4.0, 4); -- 4kg of Bell Peppers

-- Create Orders for Yael Goldberg (userID = 1004)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (5, 1004, 1, 'Dizengoff 100, Tel Aviv', 32.50)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Yael's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (5, 1, 2.5, 3), -- 2.5kg of Organic Apples
    (5, 3, 3.0, 2), -- 3kg of Carrots
    (5, 9, 5.0, 3); -- 5kg of Sweet Potatoes

-- Order 2
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (6, 1004, 2, 'Dizengoff 100, Tel Aviv', 27.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Yael's second order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (6, 2, 4.0, 2), -- 4kg of Fresh Bananas
    (6, 6, 5.0, 3); -- 5kg of Oranges

-- Create Orders for Avi Peretz (userID = 1005)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (7, 1005, 1, 'Haifa 15, Haifa', 38.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Avi's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (7, 7, 3.0, 5), -- 3kg of Broccoli
    (7, 8, 2.0, 6), -- 2kg of Spinach
    (7, 10, 2.0, 4); -- 2kg of Bell Peppers

-- Create multiple orders for Tamar Katz (userID = 1006)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (8, 1006, 1, 'Bialik 30, Ramat Gan', 25.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Tamar's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (8, 1, 3.0, 3), -- 3kg of Organic Apples
    (8, 5, 8.0, 1), -- 8kg of Potatoes
    (8, 6, 2.0, 3); -- 2kg of Oranges

-- Order 2
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (9, 1006, 2, 'Bialik 30, Ramat Gan', 30.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Tamar's second order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (9, 3, 5.0, 2), -- 5kg of Carrots
    (9, 9, 4.0, 3), -- 4kg of Sweet Potatoes
    (9, 10, 2.0, 4); -- 2kg of Bell Peppers

-- Order 3
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (10, 1006, 3, 'Bialik 30, Ramat Gan', 22.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Tamar's third order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (10, 2, 6.0, 2), -- 6kg of Fresh Bananas
    (10, 4, 2.5, 4); -- 2.5kg of Tomatoes

-- Create Orders for Noam Avraham (userID = 1007)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (11, 1007, 1, 'Weizmann 40, Rehovot', 40.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Noam's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (11, 1, 4.0, 3), -- 4kg of Organic Apples
    (11, 4, 3.0, 4), -- 3kg of Tomatoes
    (11, 7, 2.0, 5), -- 2kg of Broccoli
    (11, 9, 3.0, 3); -- 3kg of Sweet Potatoes

-- Create Orders for Michal Friedman (userID = 1008)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (12, 1008, 1, 'Herzl 75, Netanya', 36.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Michal's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (12, 2, 3.0, 2), -- 3kg of Fresh Bananas
    (12, 6, 4.0, 3), -- 4kg of Oranges
    (12, 8, 3.0, 6); -- 3kg of Spinach

-- Create Orders for Yosef Shapira (userID = 1009)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (13, 1009, 1, 'Ben Gurion 20, Beer Sheva', 29.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Yosef's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (13, 3, 3.5, 2), -- 3.5kg of Carrots
    (13, 5, 10.0, 1), -- 10kg of Potatoes
    (13, 10, 3.0, 4); -- 3kg of Bell Peppers

-- Create Orders for Shira Rosenberg (userID = 1010)
-- Order 1
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (14, 1010, 1, 'Jabotinsky 50, Petah Tikva', 33.00)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Shira's first order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (14, 1, 3.0, 3), -- 3kg of Organic Apples
    (14, 4, 2.0, 4), -- 2kg of Tomatoes
    (14, 6, 3.0, 3), -- 3kg of Oranges
    (14, 9, 2.0, 3); -- 2kg of Sweet Potatoes

-- Order 2
INSERT INTO ORDERS (orderID, userID, userOrderId, deliveryAddress, totalPrice)
VALUES (15, 1010, 2, 'Jabotinsky 50, Petah Tikva', 27.50)
ON CONFLICT (orderID) DO NOTHING;

-- Insert order items for Shira's second order
INSERT INTO ORDER_ITEMS (orderID, productID, quantityKG, pricePerKG)
VALUES
    (15, 2, 2.5, 2), -- 2.5kg of Fresh Bananas
    (15, 7, 2.0, 5), -- 2kg of Broccoli
    (15, 8, 2.0, 6); -- 2kg of Spinach

-- Reset the sequence for orders to start after our manually inserted orders
SELECT setval('orders_orderid_seq', 15, true);

-- Reset the sequence for order items to start after our manually inserted items
SELECT setval('order_items_serialid_seq', (SELECT MAX(serialID) FROM ORDER_ITEMS), true);
