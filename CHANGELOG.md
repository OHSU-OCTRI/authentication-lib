# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Add a custom `RequestRejectedHandler` implementation that returns an error response without throwing an exception. To use this implementation in your application, add a `@Bean` method that returns an instance of `LoggingRequestRejectedHandler`. (CIS-2703)

- Allow using email addresses as usernames. To avoid breaking changes, this is disabled by default. See [UPGRADING.md](./UPGRADING.md) for details on how to enable this feature. (AUTHLIB-134)

## [1.0.1] - 2024-04-18

### Fixed

- Correct session cookie properties (`server.session.cookie.*` -> `server.servlet.session.cookie.*`). To prevent making a breaking change, the default property values are unchanged. (AUTHLIB-129) For most applications, you should consider adopting the following secure values:

  ```properties
  server.servlet.session.cookie.http-only=true
  server.servlet.session.cookie.same-site=strict
  server.servlet.session.cookie.secure=true
  ```

## [1.0.0] - 2024-01-11

### Changed

- **Breaking**: Upgrade to Spring Boot 3.2 and Java 17. Consuming apps will also need to upgrade their Spring Boot and Java versions. (AUTHLIB-124)
- **Breaking**: Remove `@EnableJpaAuditing` annotation from `DefaultSecurityConfigurer`. This eliminates the need to set `spring.main.allow-bean-definition-overriding=true` in every consuming app, but it requires consuming apps to set `@EnableJpaAuditing`. (AUTHLIB-122)

### Fixed

- Implement `Serializable` in `AbstractEntity` to fix warnings when applications reference the `User` entity (AUTHLIB-120)

### Removed

- Remove unused, long-deprecated `isValidResetToken` method from the `PasswordResetTokenService`. This was replaced by the `isExpired` method of the `PasswordResetToken` entity.
- Remove unused three-argument version of the `sendPasswordResetTokenEmail` method from the `UserService`. This was only ever used by SHIFT, and was replaced by the four-argument version.

## [0.11.0] - 2023-10-25

### Added

- **Breaking**: Integrate session event logging from SHIFT. Consuming apps must add a migration for a new table. (AUTHLIB-128)


## [0.10.0] - 2023-08-07

### Changed

- **Breaking**: Upgrade to Spring Security 5.8. Consuming apps must override the Spring Security version in `pom.xml`. (AUTHLIB-123)

## [0.9.3] - 2023-06-09

### Changed

- Upgrade to Spring Boot 2.7.12 (AUTHLIB-121)

## [0.9.2] - 2023-01-23

### Fixed

- Fix authentication failure when only LDAP is enabled (AUTHLIB-118)

## [0.9.1] - 2022-08-08

### Added

- Refine SAML support (AUTHLIB-116)

### Fixed

- Ensure SAML logout configuration is generated (AUTHLIB-117)

## [0.9.0] - 2022-07-28

### Added

- Integrate SAML support developed for Opt Out Portal (AUTHLIB-96)

### Changed

- **Breaking**: Refactor to remove use of `WebSecurityConfigurerAdapter`. Consuming apps must refactor their security configuration. (AUTHLIB-103)

### Fixed

- Fix crash when `spring.main.allow-bean-definition-overriding=false` (AUTHLIB-104)

## [0.8.1] - 2022-07-18

### Fixed

- Fix missing "Forgot password" link (AUTHLIB-118)

## [0.8.0] - 2022-07-14

### Added

- **Breaking**: Require configuring the email domain associated with LDAP accounts. Consuming apps must add a configuration property to prevent a crash at startup. (AUTHLIB-110)
- Add support for content security policy (AUTHLIB-113)

### Changed

- Reduce log level of expected exceptions (AUTHLIB-91)
- Don't log when unknown email is submitted in "forgot password" (AUTHLIB-77)

### Fixed

- Fix crash when only table-based auth is enabled (AUTHLIB-112)

## [0.7.3] - 2022-06-30

### Added

- Add built-in Kubernetes probes to default public routes (AUTHLIB-88)

### Fixed

- Replace inline scripts that caues content security policy violations (CVSS-47)

## [0.7.2] - 2022-06-23

### Fixed

- Ensure that generated URLs contain the context path (AUTHLIB-108)

## [0.7.1] - 2022-06-21

### Added

- Log an error if table-based auth is enabled, but `octri.authentication.base-url` is not set (AUTHLIB-105)

### Fixed

- Fix circular reference error involving `PasswordResetTokenService` (AUTHLIB-106)
- Fix circular reference error involving `FilterBasedLdapUserSearch` (AUTHLIB-107)

## [0.7.0] - 2022-06-16

### Changed

- Upgrade to Spring Boot 2.6 (AUTHLIB-101)
- Use new `snake_case` ID convention in forms

### Fixed

- Fix circular bean reference involving `PasswordEncoder` (AUTHLIB-92)

## [0.6.0] 2022-04-09

### Changed

- **Breaking**: Support UI with Bootstrap 4 and Bootstrap 5. Consuming apps must add a UI dependency to `pom.xml`. (AUTHLIB-99)

## [0.5.5] - 2020-03-09

## Fixed

- Fix a bug in 0.5.4 that prevents application startup (AUTHLIB-87)

## [0.5.4] - 2020-03-09

_DO NOT USE: This release contains a bug that prevents application startup in some cases. Use release 0.5.5 instead._

### Fixed

- Prevent disabled and locked users from resetting their password (AUTHLIB-80)
- Prevent entry of duplicate email addresses (AUTHLIB-83)
- Prevent password reset using an expired reset token (AUTHLIB-86)

## [0.5.3] - 2020-02-25

### Fixed

- Prevent runtime exceptions when passwords fail validation (AUTHLIB-82)

## [0.5.2] - 2020-02-10

### Fixed

- Prevent creation of users with duplicate email addresses (AUTHLIB-78)

## [0.5.1] - 2019-10-18

### Added

- Include the application's `displayName` in email subjects to better indicate the source of messages (AUTHLIB-76)

## [0.5.0] - 2019-10-07

### Added

- Enable users to change their email address and reset their password (SHIFT-186)

### Removed

- **Breaking**: Remove automatic migrations. Consuming apps must opt into migrations by adding files to their migration directory. (AUTHLIB-75)

## [0.4.0] - 2019-09-27

### Added

- Add support for generating random, single-use passwords matching a pattern (SHIFT-185)
- If missing, require user to set email address on initial login (SHIFT-189)

## [0.3.0] - 2019-08-29

### Added

- Improve visibility of password validation rules and give better feedback about invalid passwords (AUTHLIB-31)

## [0.2.1] - 2019-08-06

### Fixed

- Reset consecutive login failures on password reset (AUTHLIB-67)

## [0.2.0] - 2019-07-11

### Added

- **Breaking**: Add required `octri.authentication.base-url` property. Consuming apps must add this configuration property to prevent a crash at startup.
- Allow creating users without an email address (SHIFT-102)

### Changed

- Expose Actuator health and Prometheus metrics endpoints

## [0.1.4] - 2019-01-02

- Return to user list when canceling user create / edit form (AUTHLIB-54)

## [0.1.3] - 2018-12-13

### Changed

- Upgrade to Spring Boot 2.1.0 (AUTHLIB-52)

## [0.1.2] - 2018-08-14

### Added

- Improve behavior of the LDAP user button (AUTHLIB-41)

## [0.1.1] - 2018-08-13

### Changed

- Minor changes for Cognitive Demands (AUTHLIB-36)
- Update dependencies (AUTHLIB-41)

## [0.1.0] - 2018-08-10

### Changed

- Update dependencies (AUTHLIB-41)
- **Breaking**: Use mustache for templates

## [0.0.2] - 2018-06-21

### Added

- Distinguish LDAP from table-based users (AUTHLIB-1)
- Configurable session timeout (AUTHLIB-4)
- Allow table-based users to reset their own password (AUTHLIB-5)
- Update password logic to handle LDAP users correctly (AUTHLIB-7)

### Changed

- Extract business logic from `UserController` (AUTHLIB-24)

### Fixed

- Hide navbar items based on roles (AUTHLIB-25)
- Prevent creation of table-based users when table-based auth is disabled (AUTHLIB-36)

[unreleased]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v1.0.1...HEAD
[1.0.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v1.0.0...v1.0.1
[1.0.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.11.0...v1.0.0
[0.11.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.10.0...v0.11.0
[0.10.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.9.3...v0.10.0
[0.9.3]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.9.2...v0.9.3
[0.9.2]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.9.1...v0.9.2
[0.9.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.9.0...v0.9.1
[0.9.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.8.1...v0.9.0
[0.8.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.8.0...v0.8.1
[0.8.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.7.3...v0.8.0
[0.7.3]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.7.2...v0.7.3
[0.7.2]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.7.1...v0.7.2
[0.7.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.7.0...v0.7.0
[0.7.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.6.0...v0.7.0
[0.6.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.5...v0.6.0
[0.5.5]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.4...v0.5.5
[0.5.4]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.3...v0.5.4
[0.5.3]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.2...v0.5.3
[0.5.2]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.1...v0.5.2
[0.5.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.5.0...v0.5.1
[0.5.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.4.0...v0.5.0
[0.4.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.3.0...v0.4.0
[0.3.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.2.1...v0.3.0
[0.2.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.2.0...v0.2.1
[0.2.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.1.4...v0.2.0
[0.1.4]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.1.3...v0.1.4
[0.1.3]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.1.2...v0.1.3
[0.1.2]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.1.1...v0.1.2
[0.1.1]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.1.0...v0.1.1
[0.1.0]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/compare/v0.0.2...v0.1.0
[0.0.2]: https://source.ohsu.edu/OCTRI-Apps/authentication-lib/releases/tag/v0.0.2