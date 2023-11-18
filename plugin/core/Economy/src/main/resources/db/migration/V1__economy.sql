CREATE TABLE money (
	id										INT PRIMARY KEY HASH NOT NULL,
	pocket_money							INT NOT NULL DEFAULT 0,
	bank_money								INT NOT NULL DEFAULT 0
);
