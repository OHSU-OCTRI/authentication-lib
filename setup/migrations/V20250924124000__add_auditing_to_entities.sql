-- AUTHLIB-161: Adds auditing fields from org.octri.common.domain.AbstractEntity to
-- authentication library tables.
ALTER TABLE `user`
    ADD COLUMN `version` int,
    ADD COLUMN `created_at` datetime,
    ADD COLUMN `updated_at` datetime,
    ADD COLUMN `updated_by` varchar(320) DEFAULT NULL;

UPDATE `user` SET `version` = 0, `created_at` = NOW(), `updated_at` = NOW();

ALTER TABLE `user`
    CHANGE `version` `version` int NOT NULL,
    CHANGE `created_at` `created_at` datetime NOT NULL,
    CHANGE `updated_at` `updated_at` datetime NOT NULL;

ALTER TABLE `user_role`
    ADD COLUMN `version` int,
    ADD COLUMN `created_at` datetime,
    ADD COLUMN `updated_at` datetime,
    ADD COLUMN `updated_by` varchar(320) DEFAULT NULL;

UPDATE `user_role` SET `version` = 0, `created_at` = NOW(), `updated_at` = NOW();

ALTER TABLE `user_role`
    CHANGE `version` `version` int NOT NULL,
    CHANGE `created_at` `created_at` datetime NOT NULL,
    CHANGE `updated_at` `updated_at` datetime NOT NULL;

ALTER TABLE `session_event`
    ADD COLUMN `updated_by` varchar(320) DEFAULT NULL;

ALTER TABLE `password_reset_token`
    ADD COLUMN `version` int,
    ADD COLUMN `created_at` datetime,
    ADD COLUMN `updated_at` datetime,
    ADD COLUMN `updated_by` varchar(320) DEFAULT NULL;

UPDATE `password_reset_token` SET `version` = 0, `created_at` = NOW(), `updated_at` = NOW();

ALTER TABLE `password_reset_token`
    CHANGE `version` `version` int NOT NULL,
    CHANGE `created_at` `created_at` datetime NOT NULL,
    CHANGE `updated_at` `updated_at` datetime NOT NULL;

