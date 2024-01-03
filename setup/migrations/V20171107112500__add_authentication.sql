CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_expiration_date` datetime DEFAULT NULL,
  `account_expired` bit(1) NOT NULL,
  `account_locked` bit(1) NOT NULL,
  `consecutive_login_failures` int(11) NOT NULL,
  `credentials_expiration_date` datetime DEFAULT NULL,
  `credentials_expired` bit(1) NOT NULL,
  `email` varchar(100) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `institution` varchar(100) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(128) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username` (`username`),
  UNIQUE KEY `user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user` bigint NOT NULL,
  `user_role` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6exg8o7ha8hjm3ve2bb7bbyty` (`user_role`,`user`),
  KEY `FK_1e97vv9xu9fx2kaeivgbh1jdx` (`user`),
  CONSTRAINT `user_user_role_user` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `user_user_role_user_role` FOREIGN KEY (`user_role`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
