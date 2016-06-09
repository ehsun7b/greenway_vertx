CREATE TABLE tbl_access (
     `id` INT NOT NULL AUTO_INCREMENT,
     `json` text NOT NULL,
     `time` timestamp NOT NULL,
     primary key (id)
);

CREATE TABLE tbl_chat_state (
     `id` INT NOT NULL AUTO_INCREMENT,
     `chat_id` INT NOT NULL,
     `state` varchar(255) NOT NULL,
     `json` text,
     `time` timestamp NOT NULL,
     primary key (id)
);

CREATE TABLE tbl_youtube_video (
     `id` VARCHAR(255) NOT NULL,     
     `url` VARCHAR(255) NOT NULL,
     `filename` VARCHAR(255),
     `file_id` VARCHAR(255),
     `time` timestamp NOT NULL,
     primary key (id)
);