CREATE TABLE user_tokens (
id SERIAL PRIMARY KEY,
token VARCHAR(255),
user_id INTEGER REFERENCES users (id),
expiration TIMESTAMP
);
