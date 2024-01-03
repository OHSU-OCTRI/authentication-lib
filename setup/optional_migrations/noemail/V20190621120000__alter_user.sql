-- SHIFT-102 remove email requirement
ALTER TABLE `user`
  MODIFY `email` varchar(100) DEFAULT NULL;