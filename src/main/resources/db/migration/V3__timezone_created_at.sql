ALTER TABLE scores
ALTER COLUMN created_at
TYPE timestamp with time zone
USING created_at AT TIME ZONE 'Asia/Kolkata';