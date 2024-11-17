DROP TABLE IF EXISTS TAG;
DROP TABLE IF EXISTS USERS;

CREATE TABLE tag (
                     tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     title VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       picture VARCHAR(255),
                       role VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE study (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       path VARCHAR(255) UNIQUE NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       short_description VARCHAR(255) NOT NULL,
                       full_description TEXT NOT NULL,
                       image TEXT,
                       published_date_time TIMESTAMP,
                       closed_date_time TIMESTAMP,
                       recruiting_updated_date_time TIMESTAMP,
                       recruiting BOOLEAN DEFAULT FALSE,
                       published BOOLEAN DEFAULT FALSE,
                       closed BOOLEAN DEFAULT FALSE,
                       use_banner BOOLEAN DEFAULT FALSE,
                       member_count INT DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE study_manager (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               study_id BIGINT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 시간
                               CONSTRAINT fk_study_manager_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
                               CONSTRAINT fk_study_manager_study FOREIGN KEY (study_id) REFERENCES study (id) ON DELETE CASCADE
);
