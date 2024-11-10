-- Сначала создаем основные таблицы
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
                                          description TEXT,
                                          slug VARCHAR(255) UNIQUE,
                                          created_at TIMESTAMP,
                                          updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        description TEXT,
                                        price DECIMAL(19,2),
                                        category_id BIGINT REFERENCES categories(id),
                                        size VARCHAR(20),
                                        weight DOUBLE PRECISION,
                                        is_custom BOOLEAN DEFAULT false,
                                        main_image BYTEA,
                                        is_available BOOLEAN DEFAULT true,
                                        preparation_time INTEGER,
                                        minimum_order_time INTEGER,
                                        reviews_count INTEGER DEFAULT 0,
                                        created_at TIMESTAMP,
                                        updated_at TIMESTAMP
);

-- Затем создаем связанные таблицы
CREATE TABLE IF NOT EXISTS product_ingredients (
                                                   product_id BIGINT NOT NULL,
                                                   ingredient VARCHAR(100) NOT NULL,
                                                   CONSTRAINT fk_product_ingredients FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_size_prices (
                                                   product_id BIGINT NOT NULL,
                                                   size VARCHAR(20),
                                                   price DECIMAL(19,2),
                                                   CONSTRAINT fk_product_size_prices FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
                                       id BIGSERIAL PRIMARY KEY,
                                       product_id BIGINT NOT NULL,
                                       rating INTEGER,
                                       content TEXT,
                                       author_name VARCHAR(255),
                                       author_email VARCHAR(255),
                                       created_at TIMESTAMP,
                                       approved BOOLEAN DEFAULT false,
                                       CONSTRAINT fk_product_reviews FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
