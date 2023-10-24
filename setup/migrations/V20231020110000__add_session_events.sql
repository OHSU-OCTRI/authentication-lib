-- Create table for entity SessionEvent
DROP TABLE IF EXISTS `session_event`;
CREATE TABLE `session_event` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`version` int NOT NULL,
	`created_at` datetime NOT NULL,
	`updated_at` datetime NOT NULL,
	`event` varchar(255) NOT NULL,
	`session_id` varchar(255) NOT NULL,
	`user` bigint NOT NULL,
	`as_user` bigint,
	primary key(`id`),
	CONSTRAINT session_event_user_fk FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
