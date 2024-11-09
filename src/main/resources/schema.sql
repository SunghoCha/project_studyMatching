DROP TABLE IF EXISTS TAG;

CREATE TABLE tag (
                     tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     title VARCHAR(255) NOT NULL UNIQUE
);