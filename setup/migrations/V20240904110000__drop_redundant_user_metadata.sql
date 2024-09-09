-- AUTHLIB-143: Drop redundant boolean flags for account and credential expiration that can
-- get out of sync with the corresponding expiration dates.
UPDATE `user` SET `account_expiration_date` = NOW()
    WHERE `account_expiration_date` IS NULL AND `account_expired` = 1;

UPDATE `user` SET `credentials_expiration_date` = NOW()
    WHERE `password` IS NOT NULL AND `credentials_expiration_date` IS NULL AND `credentials_expired` = 1;

ALTER TABLE `user`
    DROP COLUMN `account_expired`,
    DROP COLUMN `credentials_expired`;
