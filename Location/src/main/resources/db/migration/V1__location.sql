CREATE TABLE location (
	id										INT NOT NULL,
	location_x								DOUBLE NOT NULL,
	location_y								DOUBLE NOT NULL,
	location_z								DOUBLE NOT NULL,
	location_pitch							FLOAT NOT NULL,
	location_yaw							FLOAT NOT NULL,
	location_world							UUID NOT NULL,
	PRIMARY KEY (id)
);
