-- Create or use the weather_conditions database
CREATE DATABASE IF NOT EXISTS weather_conditions;
USE weather_conditions;

-- Users table: stores the registered users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    type VARCHAR(20) DEFAULT 'regular'
);

-- Searches table: stores the search queries made by users
CREATE TABLE IF NOT EXISTS searches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    search_query VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Example insert to test the users table
INSERT INTO users (username, password, email) VALUES ('testuser', 'password123', 'testuser@example.com');

-- Verify data is inserted correctly (optional)
SELECT * FROM users;
