CREATE TABLE knockout (
	id										UUID NOT NULL,
	knocked_out_at							TIMESTAMP,
	knockout_cause							ENUM('TODO'),
	PRIMARY KEY (id)
);
