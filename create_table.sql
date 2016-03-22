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