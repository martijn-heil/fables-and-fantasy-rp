ALTER TABLE chat ADD COLUMN chat_spy_enabled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE chat ADD COLUMN chat_spy_exclude_channels JAVA_OBJECT ARRAY;
