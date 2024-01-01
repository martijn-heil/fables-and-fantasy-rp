CREATE TABLE shop (
	id					INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY HASH NOT NULL,
	location_x			INT NOT NULL,
	location_y			INT NOT NULL,
	location_z			INT NOT NULL,
	world				UUID NOT NULL,
	owner				INT,
	item				JAVA_OBJECT NOT NULL,
	last_active			TIMESTAMP NOT NULL,
	amount				INT NOT NULL,
	buy_price			INT NOT NULL,
	sell_price			INT NOT NULL,
	stock				INT NOT NULL
);
