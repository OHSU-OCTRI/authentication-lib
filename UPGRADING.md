# Upgrading

## Upgrading to 3.2.0

Release 3.2.0 removes the `/actuator/prometheus` endpoint from the list of routes that are public by default to avoid potentially leaking information to attackers. To restore the previous behavior (not recommended), you can add `/actuator/prometheus` to your application's custom public routes.

```properties
octri.authentication.routes.custom-public-routes=/some/other/route, /actuator/prometheus
```

## Upgrading to 3.0.0

Release 3.0.0 removes deprecated code that duplicates features of other [OCTRI libraries](https://github.com/OHSU-OCTRI/). Before upgrading to this release, upgrade to version 2.3.0 and fix any code that causes deprecation warnings. Deprecated classes and properties, along with their suggested replacements, are detailed below.

### AbstractEntity

The `AbstractEntity` class that is the parent of the library's data entity classes has been replaced by the more robust implementation from the [OCTRI common library](https://github.com/OHSU-OCTRI/common-lib/). Because the common library's `AbstractEntity` has additional fields for JPA auditing, you will need to migrate your application's tables to add the new columns.

The following example migration for MySQL is provided to facilitate upgrading existing applications.

* [setup/migrations/V20250924124000__add_auditing_to_entities.sql](./setup/migrations/V20250924124000__add_auditing_to_entities.sql)

If you haven't done so already, add the [`@EnableJpaAuditing`](https://docs.spring.io/spring-data/jpa/reference/auditing.html#jpa.auditing.configuration) annotation to your application.

```java
package org.octri.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "org.octri.example", "org.octri.authentication" })
@EntityScan(basePackages = { "org.octri.example", "org.octri.authentication" })
@EnableJpaRepositories(basePackages = { "org.octri.example", "org.octri.authentication" })
@EnableJpaAuditing
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
}
```

### Email

Code for sending account-related emails has been replaced by equivalent functionality from the [OCTRI messaging library](https://github.com/OHSU-OCTRI/messaging-lib/). If your application has table-based authentication enabled, you will need to configure the library's email delivery strategy properties.

* Set `octri.messaging.enabled=true` (this is the default).
* Set `octri.messaging.email-delivery-strategy`:
** `octri.messaging.email-delivery-strategy=LOG` for development and test environments that should not deliver email
** `octri.messaging.email-delivery-strategy=SMTP` for production environments that should deliver email
* Set `octri.authentication.account-message-email` to the email address that should be used in the from: line of account-related messages.

Previous releases of the library added properties to the `spring.mail` namespace. These properties have been removed.

* `spring.mail.enabled`: This property never actually influenced the library's behavior. It can be removed.
* `spring.mail.from`: This property has been replaced by the `octri.authentication.account-message-email` property (see above).

### View Classes

Classes in the [`org.octri.authentication.server.view` package](https://github.com/OHSU-OCTRI/authentication-lib/tree/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view) that were not specific to authentication have been moved to the [OCTRI common library](https://github.com/OHSU-OCTRI/common-lib/). Classes and methods in this package have `@Deprecated` annotations and documentation comments detailing their common-lib replacements.

Affected classes:

* [`EntitySelectOption`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/EntitySelectOption.java)
* [`EnumOptionList`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/EnumOptionList.java)
* [`EnumSelectOption`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/EnumSelectOption.java)
* [`Identified`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/Identified.java)
* [`Labelled`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/Labelled.java)
* [`OptionList`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/OptionList.java)
* [`SelectOption`](https://github.com/OHSU-OCTRI/authentication-lib/blob/v2.3.0/authentication_lib/src/main/java/org/octri/authentication/server/view/SelectOption.java)

## Upgrading to 2.2.0

Release 2.2.0 includes support for notifying users when their session is about to expire. When enabled, a modal dialog is displayed when the user's session is about to expire, with the option to stay logged in. If the user does not interact with the dialog before the session times out, they are automatically logged out.

See [SESSION_TIMEOUT_NOTIFICATION.md](docs/SESSION_TIMEOUT_NOTIFICATION.md) for instructions on using this feature.

## Upgrading to 2.0.0

Release 2.0.0 significantly improves the flexibility of the user management interface. Many old and infrequently-used methods have also been eliminated.

Before upgrading to this release, you may want to upgrade your application to AuthLib 1.3.0 and fix any code that uses deprecated methods. This will reduce the number of breaking changes that will need to be addressed.

### Database Changes

This release includes several changes to the `User` entity that will require database migration. This includes:

* Removal of the `NOT NULL` constraint on the `institution` column
* Removal of redundant columns `account_expired` and `credentials_expired` in favor of the corresponding timestamp columns
* Addition of a column that stores the method that should be used to authenticate the user account

The following example migrations are provided to facilitate upgrading existing applications.

* [`setup/migrations/V20240731121000__alter_user_optional_institution.sql`](setup/migrations/V20240731121000__alter_user_optional_institution.sql)
* [`setup/migrations/V20240904110000__drop_redundant_user_metadata.sql`](setup/migrations/V20240904110000__drop_redundant_user_metadata.sql)
* [`setup/migrations/V20240910090000__add_user_auth_type.sql`](setup/migrations/V20240910090000__add_user_auth_type.sql)

The migrations may need to be modified for your application, so you should carefully review each migration and consider how your application will be impacted before upgrading.

### User Management Changes

The [`UserController` class](authentication_lib/src/main/java/org/octri/authentication/server/controller/UserController.java) has been refactored to allow overriding the default user management workflow without extending the `UserController` class. This should greatly reduce the number of cases where applications need to duplicate or extend the `UserController` class, but consuming applications will need to make changes to accommodate the updated controller.

The `UserController` class now provides separate endpoints for user creation and user updates, which requires updates to application templates. To upgrade, search for the old path (`{{req.contextPath}}/admin/user/form`), then replace it with the correct new URL for the action being taken:

* To get the new user form: `{{req.contextPath}}/admin/user/new`
* To submit the new user form: `{{req.contextPath}}/admin/user/create`
* To get the user update form: `{{req.contextPath}}/admin/user/{id}`
* To submit the user update form: `{{req.contextPath}}/admin/user/update`

The methods of `UserController` now consistently return a `ModelAndView` object instead of a template path string. Applications that have extended the `UserController` class will need to update their method signatures accordingly. Alternatively, you may be able to eliminate your custom `UserController` by using the extension mechanism described below.

The most consequential change in this release is the new [`UserManagementCustomizer`](authentication_lib/src/main/java/org/octri/authentication/server/customizer/UserManagementCustomizer.java) interface. This interface provides hooks into the `UserController, allowing consuming applications to modify the behavior when user accounts are created or updated, simply by providing a custom `UserManagementCustomizer` bean. This should enable a wide range of custom behavior, including:

* Custom validation
* Application-specific notifications
* Creation of additional records related to the user (with or without a custom form)
* Calls to external services

A detailed description of the new interface may be found in  [docs/USER_MANAGEMENT_CUSTOMIZATION.md](./docs/USER_MANAGEMENT_CUSTOMIZATION.md).


### Bootstrap 4 No Longer Supported

Bootstrap 4 has reached end of life status, so application templates styled with Bootstrap 4 are no longer supported. Applications that currently depend on `authentication_ui_bootstrap4` should upgrade to`authentication_ui_bootstrap5` and update their templates to use Bootstrap 5 conventions, or they should [override all of the AuthLib templates](docs/CONFIGURATION_PROPERTIES.md#template-configuration).

## Upgrading to 1.1.0

Release 1.1.0 includes support for using email addresses as usernames. To upgrade to this version, add [the migration to increase the size of the username column](./setup/migrations/V20240709104000__alter_user_enlarge_columns.sql) to your application. Then, to allow using email addresses as usernames, set `octri.authentication.username-style` to `email` or `mixed`.

Support for using email address for the username of LDAP is not supported in this release, but this may change in the future.

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