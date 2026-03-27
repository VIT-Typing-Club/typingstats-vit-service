ALTER TABLE users
DROP COLUMN IF EXISTS last_typegg_sync,
ADD COLUMN last_typegg_auto_sync TIMESTAMP WITH TIME ZONE,
ADD COLUMN last_typegg_manual_sync TIMESTAMP WITH TIME ZONE;