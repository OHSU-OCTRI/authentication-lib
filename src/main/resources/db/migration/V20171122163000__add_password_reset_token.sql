-- -----------------------------------------------------------------------------
-- Password Reset Token
-- -----------------------------------------------------------------------------

CREATE TABLE `password_reset_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `expiry_date` datetime NOT NULL,
  `user` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_password_reset_token_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
