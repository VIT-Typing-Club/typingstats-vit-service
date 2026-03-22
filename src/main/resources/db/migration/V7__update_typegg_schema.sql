-- V8: Rename consistency to pp and add TypeGG sync tracker

ALTER TABLE typegg_scores
RENAME COLUMN consistency TO pp;

ALTER TABLE users
ADD COLUMN last_typegg_sync TIMESTAMP WITH TIME ZONE;