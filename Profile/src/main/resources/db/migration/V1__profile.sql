CREATE TABLE profile (
	id										INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY HASH NOT NULL,
	description								VARCHAR,
	active									BOOLEAN NOT NULL,
	owner									UUID NOT NULL
);
CREATE INDEX profile_owner ON profile(owner);