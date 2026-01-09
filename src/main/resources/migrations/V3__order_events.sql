CREATE TABLE IF NOT EXISTS order_status_events (
    id uuid PRIMARY KEY DEFAULT uuidv7(),
    status varchar(50) NOT NULL,
    description varchar(255),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    order_id uuid NOT NULL REFERENCES orders (id) ON DELETE CASCADE
);