-- name: find-by-email
-- Finds a user by it's name
select id, name, email, password, date
from users
where email = :email

-- name: insert-user<!
-- Creates a user
insert into users (
  name,
  email,
  password,
  date
) values (
  :name,
  :email
  :password,
  NOW()
)

-- name: find-active-token
-- Finds a token with the expiration date greater than now
select *
from user_tokens
where token = :token
and expiration > now()
