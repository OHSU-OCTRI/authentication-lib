-- -----------------------------------------------------------------------------
-- Add default roles
-- -----------------------------------------------------------------------------

INSERT INTO user_role (description, role_name)
VALUES ('Basic User', 'ROLE_USER'),
       ('Administrator', 'ROLE_ADMIN'),
       ('Super User', 'ROLE_SUPER');

