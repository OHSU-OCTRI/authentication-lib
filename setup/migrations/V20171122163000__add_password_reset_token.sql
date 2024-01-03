-- -----------------------------------------------------------------------------
-- Password Reset Token
-- -----------------------------------------------------------------------------

CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `user` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `password_reset_token_token_uk` (`token`),
  CONSTRAINT `user_password_reset_token_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
