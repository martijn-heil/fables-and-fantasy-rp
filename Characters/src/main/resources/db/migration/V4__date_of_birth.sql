ALTER TABLE characters DROP COLUMN age;
ALTER TABLE characters ADD COLUMN date_of_birth_epoch_day BIGINT;
ALTER TABLE characters ADD COLUMN date_of_natural_death_epoch_day BIGINT;
