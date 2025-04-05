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
    deliveryAddress INTEGER,
    totalPrice INTEGER
);

-- Create INVENTORY table
CREATE TABLE IF NOT EXISTS INVENTORY (
    productID SERIAL PRIMARY KEY,
    description TEXT,
    quantityKG INTEGER,
    stockKG INTEGER,
    pricePerKG INTEGER
);

-- Create ORDER_ITEMS table
CREATE TABLE IF NOT EXISTS ORDER_ITEMS (
    serialID SERIAL PRIMARY KEY,
    orderID INTEGER NOT NULL REFERENCES ORDERS(orderID),
    productID INTEGER NOT NULL REFERENCES INVENTORY(productID),
    quantityKG INTEGER,
    pricePerKG INTEGER
);

-- Create REVIEWS table
CREATE TABLE IF NOT EXISTS REVIEWS (
    productID INTEGER REFERENCES INVENTORY(productID) PRIMARY KEY,
    numberOfRatings INTEGER,
    sumOfReviews INTEGER
);

-- Make sure your INSERT statements are correct and being executed
INSERT INTO INVENTORY (description, quantityKG, stockKG, pricePerKG) VALUES
    ('Organic Apples', 150, 200, 3),
    ('Fresh Bananas', 100, 120, 2),
    ('Carrots', 80, 90, 2),
    ('Tomatoes', 60, 70, 4),
    ('Potatoes', 200, 250, 1),
    ('Oranges', 120, 140, 3),
    ('Broccoli', 40, 45, 5),
    ('Spinach', 30, 35, 6),
    ('Sweet Potatoes', 90, 100, 3),
    ('Bell Peppers', 50, 60, 4);
