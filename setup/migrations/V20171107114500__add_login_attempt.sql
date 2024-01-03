CREATE TABLE `login_attempt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempted_at` datetime NOT NULL,
  `error_message` text,
  `error_type` text,
  `successful` bit(1) NOT NULL,
  `username` varchar(255) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `version` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;