CREATE TABLE rules (
	player_instance 	INT NOT NULL,
	world 				UUID NOT NULL,
	action				ENUM('ALLOWED', 'DENIED', 'BOUND') NOT NULL,

	PRIMARY KEY(player_instance, world)
);
