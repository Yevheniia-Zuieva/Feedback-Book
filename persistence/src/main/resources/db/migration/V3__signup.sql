ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(128);
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS nickname VARCHAR(128);
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(32) DEFAULT 'REGISTERED';
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255);

UPDATE users SET email = 'reader1@example.com', nickname = 'reader1' WHERE username = 'reader1';
UPDATE users SET email = '${mailAdmin}', nickname = 'admin' WHERE username = 'admin';

UPDATE users SET email = username WHERE email IS NULL AND username LIKE '%_@_%._%';
UPDATE users SET email = CONCAT('user_', id, '@example.com') WHERE email IS NULL;
UPDATE users SET nickname = email WHERE nickname IS NULL;
UPDATE users SET verified = TRUE WHERE verified IS FALSE;

-- Примусово перезаписуємо некоректні паролі на BCrypt-хеш пароля 'pass123'
UPDATE users SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1bC.KGFyvB7a' WHERE password IS NULL OR password NOT LIKE '$2a$%';

ALTER TABLE users ALTER COLUMN email SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);