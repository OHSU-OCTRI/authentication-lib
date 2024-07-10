-- AUTHLIB-134: Increases the size of the username and email fields
ALTER TABLE `user`
	MODIFY `username` varchar(320) NOT NULL,
	MODIFY `email` varchar(320);

ALTER TABLE `login_attempt`
	MODIFY `username` text NOT NULL;
