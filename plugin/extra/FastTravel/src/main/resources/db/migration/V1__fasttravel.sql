CREATE TABLE fasttravel (
	id										IDENTITY NOT NULL PRIMARY KEY,
	from_region								VARCHAR NOT NULL UNIQUE,
	to_x									DOUBLE NOT NULL,
	to_y									DOUBLE NOT NULL,
	to_z									DOUBLE NOT NULL,
	to_yaw									FLOAT NOT NULL,
	to_pitch								FLOAT NOT NULL,
	to_world								UUID NOT NULL,
	travel_duration							INT NOT NULL DEFAULT 60
);
