CREATE TABLE playerinstance (
	id										IDENTITY NOT NULL PRIMARY KEY,
	owner									UUID NOT NULL
);
CREATE INDEX playerinstance_owner ON playerinstance(owner);
