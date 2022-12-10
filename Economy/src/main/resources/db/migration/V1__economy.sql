CREATE TABLE money (
	id										INT NOT NULL,
	pocket_money							INT NOT NULL DEFAULT 0,
	bank_money								INT NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
);
