CREATE TABLE accounts (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    balance INT
);

INSERT INTO accounts (id, name, balance) VALUES (1, 'Account-1', 5000);
INSERT INTO accounts (id, name, balance) VALUES (2, 'Account-2', 2000);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    quantity INT
);
