CREATE TABLE fables_players (
	id					UUID NOT NULL,
	current_character	INTEGER,
	chat_channel		VARCHAR(32) DEFAULT 'ooc',
	PRIMARY KEY (id)
);

CREATE TABLE fables_characters (
    id 					INTEGER NOT NULL,
	player				UUID NOT NULL,
	name				VARCHAR(255) NOT NULL,
	description			TEXT,
	age					INTEGER NOT NULL,
    race				VARCHAR(32) NOT NULL,
	gender				INTEGER NOT NULL,
	money				INTEGER NOT NULL,
	location_x			FLOAT NOT NULL,
	location_y			FLOAT NOT NULL,
	location_z			FLOAT NOT NULL,
	location_yaw		FLOAT NOT NULL,
	location_pitch  	FLOAT NOT NULL,
	stat_strength		TINYINT NOT NULL,
	stat_defense		TINYINT NOT NULL,
	stat_agility		TINYINT NOT NULL,
	stat_intelligence	TINYINT NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE fables_players ADD FOREIGN KEY (current_character) REFERENCES fables_characters(id);
ALTER TABLE fables_characters ADD FOREIGN KEY (player) REFERENCES fables_players(id);
