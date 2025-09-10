-- Create category table for GraphQL API
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NULL
);

-- Add index on name for better query performance
CREATE INDEX idx_category_name ON category(name);

-- Add index on valid_from for temporal queries
CREATE INDEX idx_category_valid_from ON category(valid_from);

-- Add index on valid_to for temporal queries
CREATE INDEX idx_category_valid_to ON category(valid_to);
