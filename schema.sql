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
