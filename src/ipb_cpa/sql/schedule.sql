-- name: create-schedule-table!
-- Creates the schedule table (Used only for tests)
CREATE TABLE schedule (
  id INTEGER PRIMARY KEY,
  day_of_the_week TEXT,
  description TEXT,
  time TEXT
);

-- name: select-all-schedules
-- Retrieves all schedules
SELECT id, day_of_the_week, description, time
FROM schedule;

-- name: select-schedule
-- Retrieves a schedule by id
SELECT id, day_of_the_week, description, time
FROM schedule
WHERE id = :id;

-- name: insert-schedule!
-- Insert a new schedule in the database
INSERT INTO schedule (
  day_of_the_week,
  description,
  time
  ) VALUES (
  :day_of_the_week,
  :description,
  :time
);

-- name: update-schedule!
-- Updates an existing schedule
UPDATE schedule
SET day_of_the_week = :day_of_the_week,
    description = :description,
    time = :time
WHERE id = :id;
