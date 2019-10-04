# Setup Scripts

This directory includes SQL migrations to create database tables used to store the library's entities.

## Base Migrations

The `migrations` directory includes migrations to create the default entities:

```
migrations/
├── V20171107112500__add_authentication.sql
├── V20171107114500__add_login_attempt.sql
├── V20171108163000__add_default_roles.sql
├── V20171122163000__add_password_reset_token.sql
└── V20180807143000__add_constraints.sql
```

If you used the OCTRI Spring Boot archetype to generate your application, these migrations are already included.

## Optional Migrations

The `optional_migrations` directory includes migrations that alter the schema to facilitate specific workflows.

```
optional_migrations/
└── noemail
    ├── README.md
    └── V20190621120000__alter_user.sql
```

Currently this only includes a migration that removes the requirement that users have an email address.
