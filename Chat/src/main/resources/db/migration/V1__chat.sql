CREATE TABLE chat (
	id										UUID NOT NULL,
	channel									JAVA_OBJECT,
	disabled_channels 						JAVA_OBJECT ARRAY,
	chat_style 								VARCHAR,
	reception_indicator_enabled				BOOLEAN NOT NULL DEFAULT FALSE,
	PRIMARY KEY (id)
);
