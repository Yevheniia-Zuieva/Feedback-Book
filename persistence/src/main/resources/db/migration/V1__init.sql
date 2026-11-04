CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    pub_year INT NOT NULL
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    book_id BIGINT NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_comment_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Тестові початкові дані
INSERT INTO books (title, author, pub_year) VALUES ('Кобзар', 'Тарас Шевченко', 1840);
INSERT INTO books (title, author, pub_year) VALUES ('1984', 'Джордж Оруелл', 1949);

INSERT INTO users (username, password, role) VALUES ('admin', 'pass123', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('reader1', 'pass123', 'USER');

INSERT INTO comments (text, book_id, user_id) VALUES ('Чудова книга!', 1, 2);
INSERT INTO comments (text, book_id, user_id) VALUES ('Шедевр класики.', 1, 1);