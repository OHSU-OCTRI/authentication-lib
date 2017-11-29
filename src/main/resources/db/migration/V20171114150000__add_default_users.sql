-------------------------------------------------------------------------------
-- User ids
-------------------------------------------------------------------------------
set @benton = 1;
set @castro = 2;
set @harrelst = 3;
set @lawhead = 4;
set @ramsdill = 5;
set @sams = 6;
set @yateam = 7;

-------------------------------------------------------------------------------
-- User Role ids
-------------------------------------------------------------------------------
set @admin = 1;
set @basic = 2;
set @super = 3;

-------------------------------------------------------------------------------
-- Insert a user
-------------------------------------------------------------------------------

INSERT INTO user (id, account_expiration_date, account_expired, account_locked,
    consecutive_login_failures, credentials_expiration_date,
    credentials_expired, email, enabled, first_name, institution, last_name,
    password, username)
VALUES
	(@benton, NULL, 0, 0, 0, NULL, 0, 'benton@ohsu.edu', 1, 'Erik', 'OHSU', 'Benton', NULL, 'benton'),
	(@castro, NULL, 0, 0, 0, NULL, 0, 'castro@ohsu.edu', 1, 'David', 'OHSU', 'Castro', NULL, 'castro'),
	(@harrelst, NULL, 0, 0, 0, NULL, 0, 'harrelst@ohsu.edu', 1, 'Heath', 'OHSU', 'Harrelson', NULL, 'harrelst'),
	(@lawhead, NULL, 0, 0, 0, NULL, 0, 'lawhead@ohsu.edu', 1, 'Matt', 'OHSU', 'Lawhead', NULL, 'lawhead'),
	(@ramsdill, NULL, 0, 0, 0, NULL, 0, 'ramsdill@ohsu.edu', 1, 'Justin', 'OHSU', 'Ramsdill', NULL, 'ramsdill'),
	(@sams, NULL, 0, 0, 0, NULL, 0, 'sams@ohsu.edu', 1, 'Weldon', 'OHSU', 'Sams', NULL, 'sams'),
	(@yateam, NULL, 0, 0, 0, NULL, 0, 'yateam@ohsu.edu', 1, 'Amy', 'OHSU', 'Yates', NULL, 'yateam');

-------------------------------------------------------------------------------
-- Insert roles
-------------------------------------------------------------------------------

INSERT INTO user_role (id, description, role_name)
VALUES
	(@admin, 'Administrator', 'ROLE_ADMIN'),
	(@basic, 'Basic User', 'ROLE_USER'),
	(@super, 'Super User', 'ROLE_SUPER');

-------------------------------------------------------------------------------
-- Insert role assignments
-------------------------------------------------------------------------------

INSERT INTO user_user_role (user, user_role)
VALUES
	(@benton, @admin), (@benton, @super),
	(@castro, @admin), (@castro, @super),
	(@harrelst, @admin), (@harrelst, @super),
	(@lawhead, @admin), (@lawhead, @super),
	(@ramsdill, @admin), (@ramsdill, @super),
	(@sams, @admin), (@sams, @super),
	(@yateam, @admin), (@yateam, @super);
