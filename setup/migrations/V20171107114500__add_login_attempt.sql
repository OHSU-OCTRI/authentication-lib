CREATE TABLE `login_attempt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attempted_at` datetime NOT NULL,
  `error_message` text COLLATE utf8_unicode_ci,
  `error_type` text COLLATE utf8_unicode_ci,
  `successful` bit(1) NOT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `ip_address` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;