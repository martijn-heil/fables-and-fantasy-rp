ALTER TABLE characters DROP COLUMN age;
ALTER TABLE characters ADD COLUMN date_of_birth_epoch_day BIGINT NOT NULL;
