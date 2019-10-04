# Noemail Migration

The included migration defaults the `user` table's `email` column to NULL and drops the 
unique index on email to facilitate uses where users may not have an email address.
