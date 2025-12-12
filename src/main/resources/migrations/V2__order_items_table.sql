CREATE TABLE IF NOT EXISTS order_items (
    id uuid PRIMARY KEY DEFAULT uuidv7(),
    product_name varchar(150),
    price numeric(12, 2) NOT NULL CHECK (price >= 0),
    quantity int NOT NULL CHECK (quantity > 0),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    product_id uuid NOT NULL,
    order_id uuid NOT NULL REFERENCES orders (id) ON DELETE CASCADE
);