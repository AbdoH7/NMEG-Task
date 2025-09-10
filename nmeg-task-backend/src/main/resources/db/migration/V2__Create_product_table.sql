-- Create product table for GraphQL API
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
);

-- Add index on name for better query performance
CREATE INDEX idx_product_name ON product(name);

-- Add index on category_id for foreign key queries
CREATE INDEX idx_product_category_id ON product(category_id);

-- Add index on description for search functionality
CREATE INDEX idx_product_description ON product(description);
