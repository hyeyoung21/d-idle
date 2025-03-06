CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100),
                       phone VARCHAR(20),
                       address TEXT,
                       user_type VARCHAR(20) CHECK (user_type IN ('customer', 'business', 'admin')) NOT NULL DEFAULT 'customer',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE businesses (
                            business_id SERIAL PRIMARY KEY,
                            user_id INT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                            business_name VARCHAR(100) NOT NULL,
                            business_number VARCHAR(50) UNIQUE NOT NULL,
                            business_address TEXT NOT NULL,
                            business_phone VARCHAR(20) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE business_approvals (
                                    approval_id SERIAL PRIMARY KEY,
                                    business_id INT UNIQUE NOT NULL REFERENCES businesses(business_id) ON DELETE CASCADE,
                                    approved_by INT NOT NULL REFERENCES users(user_id) ON DELETE SET NULL,
                                    approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected')) NOT NULL DEFAULT 'pending'
);

CREATE TABLE categories (
                            category_id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT
);

CREATE TABLE products (
                          product_id SERIAL PRIMARY KEY,
                          business_id INT NOT NULL REFERENCES businesses(business_id) ON DELETE CASCADE,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          stock_quantity INT NOT NULL DEFAULT 0,
                          category_id INT REFERENCES categories(category_id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                        total_price DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) CHECK (status IN ('pending', 'shipped', 'delivered', 'cancelled')) NOT NULL DEFAULT 'pending',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
                             order_item_id SERIAL PRIMARY KEY,
                             order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                             product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                             quantity INT NOT NULL,
                             price DECIMAL(10,2) NOT NULL
);

CREATE TABLE payments (
                          payment_id SERIAL PRIMARY KEY,
                          order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                          payment_method VARCHAR(50) NOT NULL,
                          payment_status VARCHAR(20) CHECK (payment_status IN ('pending', 'completed', 'failed')) NOT NULL DEFAULT 'pending',
                          transaction_id VARCHAR(100),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
                         review_id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                         product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                         rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
                            cart_item_id SERIAL PRIMARY KEY,
                            user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                            product_id INT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
);


-- 트리거 함수 생성
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- 트리거 생성
CREATE TRIGGER update_product_timestamp
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_timestamp();

SELECT SUM(o.total_Price) FROM Orders o JOIN Users u ON o.user_Id = u.user_id
WHERE u.business_id = :businessId;
AND o.status <> 'CANCELLED';