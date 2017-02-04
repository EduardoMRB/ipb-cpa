-- name: select-all-videos
-- Retrieves all videos
SELECT id, title, excerpt, date, embedded, active
FROM videos;

-- name: select-video
-- Retrieves a video by id
SELECT id, title, excerpt, date, embedded, active
FROM videos
WHERE id = :id;

-- name: insert-video<!
-- Inserts a new video
INSERT INTO videos (
  title,
  excerpt,
  date,
  embedded,
  active
  ) VALUES (
  :title,
  :excerpt,
  :date,
  :embedded,
  :active
);

-- name: update-video<!
-- Updates an existing video
UPDATE videos
SET title = :title,
    excerpt = :excerpt,
    date = :date,
    embedded = :embedded,
    active = :active
WHERE id = :id;

-- name: delete-video!
-- Deletes a video by id
DELETE FROM videos
WHERE id = :id;
