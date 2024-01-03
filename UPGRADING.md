# Upgrading

## Upgrading to 1.0.0

Release 1.0.0 upgrades dependencies to Spring Boot 3, and Spring Security 6. This also updates minimum Java version to Java 17. To use this version, you must upgrade your application to at least Spring Boot 3.2 and Java 17 as well.

In addition, this release removes the `@EnableJpaAuditing` annotation from the `DefaultSecurityConfigurer` class. This eliminates the need to set `spring.main.allow-bean-definition-overriding=true` in every consuming app, but it requires consuming apps to be annotated with `@EnableJpaAuditing`. This is the default for applications created using OCTRI's Spring Boot archetype, so you may not need to make application changes to accommodate it.

If your application has not been upgraded to version 0.11.0, you will need to add [the migration for the session events table](./setup/migrations/V20231020110000__add_session_events.sql). Applications that were already upgraded to version 0.11.0 should add the following migration to modify the table for Hibernate 6 and MySQL 8.

```sql
-- Convert to an enum column for Hibernate 6
ALTER TABLE `session_event`
    MODIFY COLUMN `event` enum ('LOGIN', 'LOGOUT', 'IMPERSONATION') NOT NULL;
```

Any data in the `session_event` table will be preserved. Without this migration, schema validation will fail.

## Upgrading to 0.11.0

Release 0.11.0 integrates session tracking code from SHIFT into AuthLib for use in other projects. This introduces a new `SessionEvent` entity, so upgrading requires adding the following migration to your application:

[add_session_events.sql](./setup/migrations/V20231020110000__add_session_events.sql)

See [SESSION_EVENTS.md](./docs/SESSION_EVENTS.md) for detailed architectural information.

## Upgrading to 0.10.0

To prepare for upgrades to Spring Boot 3 and Spring Security 6, the 0.10.0 release upgrades to Spring Security 5.8. To upgrade to this release, either upgrade your application from Spring Boot 2.6 to Spring Boot 2.7, or override the Spring Security version in `pom.xml` ([instructions here](https://docs.spring.io/spring-security/reference/5.8/getting-spring-security.html#getting-maven-boot)).

## Upgrading to 0.9.0

To prepare for upgrades to Spring Boot 3 and Spring Security 6, the 0.9.0 release replaces use of the deprecated `WebSecurityConfigurerAdapter` class. Applications should no longer extend the `FormSecurityConfiguration` or `BaseSecurityConfiguration` classes from AuthLib. Instead, applications should instantiate a `SecurityFilterChain` bean to configure `HttpSecurity`. A default `SecurityFilterChain` bean is provided by the [`DefaultSecurityConfigurer` class](./authentication_lib/src/main/java/org/octri/authentication/DefaultSecurityConfigurer.java) if the application does not instantiate one.

For an example of how provide a custom `SecurityFilterChain`, see [`SecurityConfiguration.java` in COVID Serology Dashboard](https://source.ohsu.edu/OCTRI-Apps/covid-serology-dashboard/blob/233d51563648389c3d093b46bb4405c3febf5e4a/src/main/java/org/octri/covid_serology_dashboard/SecurityConfiguration.java#L31-L61). This class instantiates a custom `SecurityFilterChain` bean, using methods of `DefaultSecurityConfigurer` to set up default behavior.

To reduce the need for creating a custom `SecurityFilterChain`, common cases are now handled using configuration, including custom post-login, post-logout, and public routes. These can now be configured using the `octri.authentication.routes` properties. See the [`application.properties` file in COVID Serology Dashboard](https://source.ohsu.edu/OCTRI-Apps/covid-serology-dashboard/blob/233d51563648389c3d093b46bb4405c3febf5e4a/src/main/resources/application.properties#L62-L63) and [Opt Out Portal](https://source.ohsu.edu/OCTRI-Apps/optout-boot/blob/c04b4577cb9dad500dd41b7bb994438c0c3d8690/src/main/resources/application.properties#L18-L19) for examples.

## Upgrading to 0.8.0

In prior AuthLib releases, the email domain associated with LDAP accounts was hardcoded to ohsu.edu. To support applications running at other institutions, the 0.8.0 release includes a new configuration property, `ldap.context-source.email-domain`. If your application uses LDAP authentication, you must set this property to prevent a crash at startup.

For example:

```
ldap.context-source.email-domain=ohsu.edu
```

## Upgrading to version 0.6.0

As of the 0.6.0 release, AuthLib supports UI implemented using Bootstrap 4 and using Bootstrap 5. This is done by providing a separate UI package containing templates specific to each version of Bootstrap. When upgrading, you must add the appropriate UI package to your application's `pom.xml`.

For example:

```diff
 		<dependency>
 			<groupId>org.octri.authentication</groupId>
 			<artifactId>authentication_lib</artifactId>
 			<version>${authentication_lib.version}</version>
 		</dependency>
+		<dependency>
+			<groupId>org.octri.authentication</groupId>
+			<artifactId>authentication_ui_bootstrap5</artifactId>
+			<version>${authentication_lib.version}</version>
+		</dependency>
```

This ensures that the AuthLib forms can be rendered.

Alternatively, applications can provide custom templates for the AuthLib UI. At startup, the library searches for the expected templates, and any missing templates will be logged. Attempting to visit routes with missing templates will cause an exception.

## Upgrading to version 0.5.0

With the 0.5.0 release, AuthLib no longer automatically applies database migrations using Flyway. In practice, these migrations could conflict with migrations provided by the applications, and they complicated integrating AuthLib into applications that previously used other auth solutions. AuthLib migrations must now be added to consuming applications manually. See [README.md](./README.md) for instructions.

## Upgrading to version 0.2.0

The 0.2.0 release includes a new required property, `octri.authentication.base-url`. If this property is not set, the application will crash at startup. When upgrading, ensure that `octri.authentication.base-url` is set to the environment's base URL, not including the context path. For example:

```
octri.authentication.base-url=https://octridev.ohsu.edu
```