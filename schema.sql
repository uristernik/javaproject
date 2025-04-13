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

-- Reset the sequence to start after our manually inserted user
SELECT setval('users_userid_seq', 1000, true);

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
