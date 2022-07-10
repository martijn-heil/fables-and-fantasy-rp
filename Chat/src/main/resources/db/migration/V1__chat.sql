CREATE TABLE chat (
	id					UUID NOT NULL,
	channel				JAVA_OBJECT,
	disabled_channels 	JAVA_OBJECT ARRAY,
	chat_style 			VARCHAR,
	PRIMARY KEY (id)
);
