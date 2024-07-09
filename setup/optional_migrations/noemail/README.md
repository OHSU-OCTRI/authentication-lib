# Noemail Migrations

The optional migrations in this directory are provided to support use cases where the 'noemail' profile is enabled, making the User email address field optional.

* [`V20190621120000__alter_user.sql`](./V20190621120000__alter_user.sql): Defaults the `user` table's `email` column to NULL and drops the unique index on `email`
* [`V20240709104000__alter_user_enlarge_columns.sql`](./V20240709104000__alter_user_enlarge_columns.sql): Increases the sizes of the `username` and `email` columns
