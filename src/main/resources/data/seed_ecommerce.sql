-- DDL and Seed Data for Apache Ossie Tutorial

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    customer_name VARCHAR(100),
    country VARCHAR(50),
    status VARCHAR(20)
);

CREATE TABLE products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100),
    category VARCHAR(50),
    cost_price DECIMAL(10, 2)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT REFERENCES customers(customer_id),
    order_date DATE,
    total_amount DECIMAL(10, 2),
    order_status VARCHAR(20)
);

CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY,
    order_id INT REFERENCES orders(order_id),
    product_id INT REFERENCES products(product_id),
    quantity INT,
    unit_price DECIMAL(10, 2)
);

-- Seed Customers
INSERT INTO customers VALUES (1, 'Alice Smith', 'USA', 'ACTIVE');
INSERT INTO customers VALUES (2, 'Bob Jones', 'USA', 'ACTIVE');
INSERT INTO customers VALUES (3, 'Charlie Brown', 'Canada', 'ACTIVE');
INSERT INTO customers VALUES (4, 'Diana Prince', 'UK', 'ACTIVE');
INSERT INTO customers VALUES (5, 'Evan Wright', 'Germany', 'INACTIVE');

-- Seed Products
INSERT INTO products VALUES (101, 'Mechanical Keyboard', 'Electronics', 60.00);
INSERT INTO products VALUES (102, 'Wireless Mouse', 'Electronics', 25.00);
INSERT INTO products VALUES (103, 'Ergonomic Desk Chair', 'Furniture', 180.00);
INSERT INTO products VALUES (104, 'Standing Desk Converter', 'Furniture', 150.00);
INSERT INTO products VALUES (105, 'USB-C Docking Hub', 'Electronics', 70.00);

-- Seed Orders
INSERT INTO orders VALUES (1001, 1, DATE '2026-01-15', 250.00, 'COMPLETED');
INSERT INTO orders VALUES (1002, 1, DATE '2026-02-10', 120.00, 'COMPLETED');
INSERT INTO orders VALUES (1003, 2, DATE '2026-01-22', 320.00, 'COMPLETED');
INSERT INTO orders VALUES (1004, 3, DATE '2026-02-05', 450.00, 'COMPLETED');
INSERT INTO orders VALUES (1005, 4, DATE '2026-02-18', 95.00, 'COMPLETED');
INSERT INTO orders VALUES (1006, 2, DATE '2026-03-01', 500.00, 'COMPLETED');

-- Seed Order Items
INSERT INTO order_items VALUES (501, 1001, 101, 1, 110.00);
INSERT INTO order_items VALUES (502, 1001, 102, 2, 70.00);
INSERT INTO order_items VALUES (503, 1002, 105, 1, 120.00);
INSERT INTO order_items VALUES (504, 1003, 103, 1, 320.00);
INSERT INTO order_items VALUES (505, 1004, 103, 1, 320.00);
INSERT INTO order_items VALUES (506, 1004, 102, 2, 65.00);
INSERT INTO order_items VALUES (507, 1005, 102, 1, 45.00);
INSERT INTO order_items VALUES (508, 1005, 105, 1, 50.00);
INSERT INTO order_items VALUES (509, 1006, 104, 2, 250.00);
