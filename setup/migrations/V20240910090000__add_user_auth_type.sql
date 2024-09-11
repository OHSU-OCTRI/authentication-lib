-- AUTHLIB-73: Add authentication method to user accounts to explicitly discriminate
-- LDAP accounts from other account types.
-- NOTE: You may need to alter this migration for your application, particularly if the
-- application is using SAML authentication (see comment below).

ALTER TABLE `user`
    ADD COLUMN `authentication_method` enum('LDAP','SAML','TABLE_BASED') AFTER `id`;

-- Set the authentication method to LDAP, unless a password hash is present.
-- NOTE: Change this if your application uses SAML.
UPDATE
  `user`
SET `authentication_method` = CASE
    WHEN `password` IS NOT NULL THEN 'TABLE_BASED'
    ELSE 'LDAP'
END;

ALTER TABLE `user`
    MODIFY COLUMN `authentication_method` enum('LDAP', 'SAML', 'TABLE_BASED') NOT NULL;
