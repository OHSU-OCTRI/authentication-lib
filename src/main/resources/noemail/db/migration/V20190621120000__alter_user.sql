-- SHIFT-102 remove email requirement
ALTER TABLE `user`
  MODIFY `email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  DROP INDEX `user_email`;