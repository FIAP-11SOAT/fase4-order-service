CREATE TABLE IF NOT EXISTS orders (
    id uuid PRIMARY KEY DEFAULT uuidv7(),
    status varchar(50) NOT NULL,
    status_event varchar(50),
    total_amount numeric(12, 2) NOT NULL CHECK (total_amount >= 0),
    order_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    customer_id varchar(255),
    payment_id varchar(255)
);