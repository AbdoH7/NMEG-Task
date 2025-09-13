-- V3__Create_product_image_table.sql
CREATE TABLE product_image (
   id BIGSERIAL PRIMARY KEY,
   image BYTEA NOT NULL,
   product_id BIGINT NOT NULL,
   image_order INTEGER DEFAULT 0,
   CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_image_product_id ON product_image(product_id);
CREATE INDEX idx_product_image_order ON product_image(product_id, image_order);