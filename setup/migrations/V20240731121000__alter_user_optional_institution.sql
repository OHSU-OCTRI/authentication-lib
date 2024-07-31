-- AUTHLIB-57: Make institution field optional
ALTER TABLE `user`
	MODIFY `institution` varchar(100) NULL;