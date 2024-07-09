-- AUTHLIB-134: Increases the size of the username email fields
ALTER TABLE `user`
	MODIFY `username` varchar(320) NOT NULL,
	MODIFY `email` varchar(320);