-- -----------------------------------------------------------------------------
-- If you modify this file's name also update the variable
-- `SQL="sql/add_roles_and_users.sql"` in `setup/add_roles_and_users.sh`.
-- -----------------------------------------------------------------------------

-- -----------------------------------------------------------------------------
-- Procedure for inserting a user and one for roles
-- -----------------------------------------------------------------------------

DELIMITER ;;

-- add_user()
DROP PROCEDURE IF EXISTS add_user;;
CREATE PROCEDURE add_user (IN email VARCHAR(100),
                           IN first_name VARCHAR(50),
                           IN last_name VARCHAR(50),
                           IN username VARCHAR(50),
                           OUT id BIGINT(20))
BEGIN
    INSERT INTO user (account_expiration_date, account_expired, account_locked,
        consecutive_login_failures, credentials_expiration_date,
        credentials_expired, email, enabled, first_name, institution, last_name,
        password, username)
    VALUES
        (NULL, 0, 0, 0, NULL, 0, email, 1, first_name, 'OHSU', last_name, NULL, username);
    SET id = (SELECT last_insert_id());
END;;

-- add_role()
DROP PROCEDURE IF EXISTS add_role;;
CREATE PROCEDURE add_role (IN description VARCHAR(50),
                           IN role_name VARCHAR(255),
                           OUT id BIGINT(20))
BEGIN
    INSERT INTO user_role (description, role_name)
    VALUES (description, role_name);
    SET id = (SELECT last_insert_id());
END;;
DELIMITER ;

-- -----------------------------------------------------------------------------
-- Insert default users
-- -----------------------------------------------------------------------------

CALL add_user('benton@ohsu.edu', 'Erik', 'Benton', 'benton', @benton);
CALL add_user('castro@ohsu.edu', 'David', 'Castro', 'castro', @castro);
CALL add_user('harrelst@ohsu.edu', 'Heath', 'Harrelson', 'harrelst', @harrelst);
CALL add_user('lawhead@ohsu.edu', 'Matt', 'Lawhead', 'lawhead', @lawhead);
CALL add_user('ramsdill@ohsu.edu', 'Justin', 'Ramsdill', 'ramsdill', @ramsdill);
CALL add_user('sams@ohsu.edu', 'Weldon', 'Sams', 'sams', @sams);
CALL add_user('yateam@ohsu.edu', 'Amy', 'Yates', 'yateam', @yateam);

-- -----------------------------------------------------------------------------
-- Insert roles
-- -----------------------------------------------------------------------------

CALL add_role('Basic User', 'ROLE_USER', @user);
CALL add_role('Administrator', 'ROLE_ADMIN', @admin);
CALL add_role('Super User', 'ROLE_SUPER', @super);

-- -----------------------------------------------------------------------------
-- Insert role assignments
-- -----------------------------------------------------------------------------

INSERT INTO user_user_role (user, user_role)
VALUES
	(@benton, @admin), (@benton, @super),
	(@castro, @admin), (@castro, @super),
	(@harrelst, @admin), (@harrelst, @super),
	(@lawhead, @admin), (@lawhead, @super),
	(@ramsdill, @admin), (@ramsdill, @super),
	(@sams, @admin), (@sams, @super),
	(@yateam, @admin), (@yateam, @super);

-- -----------------------------------------------------------------------------
-- Clean up
-- -----------------------------------------------------------------------------

DROP PROCEDURE IF EXISTS add_user;
DROP PROCEDURE IF EXISTS add_role;
