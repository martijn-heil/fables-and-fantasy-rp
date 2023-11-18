CREATE TABLE rule (
	profile				INT NOT NULL,
	world 				UUID NOT NULL,
	action				ENUM('ALLOWED', 'DENIED', 'BOUND') NOT NULL,

	PRIMARY KEY(profile, world)
);
