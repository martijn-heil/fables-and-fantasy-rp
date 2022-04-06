CREATE TABLE fables_players (
	id					UUID NOT NULL,
	current_character	INTEGER,
	chat_channel		VARCHAR(32) DEFAULT 'ooc',
	PRIMARY KEY (id)
);

CREATE TABLE fables_characters (
    id 					IDENTITY NOT NULL,
	player				UUID NOT NULL,
	name				VARCHAR(32) NOT NULL,
	description			TEXT,
	age					INTEGER NOT NULL,
    race				VARCHAR(32) NOT NULL,
	gender				INTEGER NOT NULL,
	money				INTEGER NOT NULL,
	created_at			TIMESTAMP WITH TIME ZONE,
	last_seen			TIMESTAMP WITH TIME ZONE,
	location_x			DOUBLE PRECISION NOT NULL,
	location_y			DOUBLE PRECISION NOT NULL,
	location_z			DOUBLE PRECISION NOT NULL,
	location_yaw		FLOAT NOT NULL,
	location_pitch  	FLOAT NOT NULL,
	stat_strength		TINYINT NOT NULL,
	stat_defense		TINYINT NOT NULL,
	stat_agility		TINYINT NOT NULL,
	stat_intelligence	TINYINT NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY (name)
);

CREATE INDEX fables_characters_player ON fables_characters(player);
CREATE INDEX fables_players_current_character on fables_players(current_character);

ALTER TABLE fables_players ADD FOREIGN KEY (current_character) REFERENCES fables_characters(id);
ALTER TABLE fables_characters ADD FOREIGN KEY (player) REFERENCES fables_players(id);
