-- -----------------------------------------------------------------------------
-- Add NOT NULL constraints for role_name and description.
-- -----------------------------------------------------------------------------

ALTER TABLE `user_role` MODIFY `role_name` VARCHAR(255) NOT NULL,
                        MODIFY `description` VARCHAR(255) NOT NULL;