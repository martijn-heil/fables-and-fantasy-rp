CREATE TABLE inventory (
	id										INT PRIMARY KEY HASH NOT NULL,
	inventory								JAVA_OBJECT NOT NULL,
	ender_chest								JAVA_OBJECT NOT NULL
);
