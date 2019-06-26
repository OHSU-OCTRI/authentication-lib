# OCTRI Authentication Library

This library allows you to use the entities, services, and templates provided to bootstrap authentication into a new Spring Boot application. Domain classes for users and roles are provided along with Flyway migrations to initiate a MySQL database. Once configured, your application will have endpoints and UI for login and user management. In addition, failure and success handlers are provided that will persist all login attempts with IP addresses for auditing purposes and lock an account after a configurable number of failed login attempts.

## Getting started

The repo 'auth_example_project' in this project shows a minimal web application that uses the library. When building your own application, add this dependency to your pom:

```
	<dependency>
		<groupId>org.octri.authentication</groupId>
		<artifactId>authentication_lib</artifactId>
		<version>${authentication_lib.version}</version>
	</dependency>
```

The library will transitively bring in several Spring Boot jars along with MySQL, Flyway, etc. In your Application definition, the Spring Boot Runner needs to set some additional parameters to ensure that domain, repositories, and autowired components for the Authentication Library are picked up. You will also likely need to include your project's package in these annotations.

```
@SpringBootApplication
@ComponentScan({"org.octri.test", "org.octri.authentication"})
@EntityScan( basePackages = {"org.octri.test", "org.octri.authentication"} )
@EnableJpaRepositories("org.octri.authentication")
public class TestProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestProjectApplication.class, args);
	}
}
```

The authentication library provides security configuration options for a web-based application [`FormSecurityConfiguration.java`](src/main/java/org/octri/authentication/FormSecurityConfiguration.java) or a REST application [`ApiSecurityConfiguration.java`](src/main/java/org/octri/authentication/ApiSecurityConfiguration.java). Either can be enabled by simply extending the configuration you want to use and adding the `@Configuration` annotation:

```
@Configuration
public class SecurityConfiguration extends FormSecurityConfiguration {

}
```

The example project has a few other pieces of configuration, including a sample landing page `home.html` and a controller to handle request mappings. These are not strictly necessary.

Configure the Spring Datasource in your Boot application. [https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html]. If using LDAP, also configure it through properties or environment variables:

```
ldap.context-source.url=
ldap.context-source.user-dn=
ldap.context-source.password=
ldap.context-source.search-base=
ldap.context-source.search-filter=
ldap.context-source.organization=
```

If using the standard Docker/MySQL setup, start the MySQL container first and create the database and user. Then start up your application and Flyway migrations for the authentication library should create some structure for users and roles.

### JavaScript

`default.js` needs access to the application context path. To provide the application context add the following meta tag to your pages. The trailing slash is required.

```
<meta name="ctx" content="{{req.contextPath}}/" />
```

### Email Configuration

Configure email using standard Spring Mail properties. Place these in your `application.properties` file.

```
spring.mail.enabled=false
spring.mail.from=octrihlp@ohsu.edu
spring.mail.default-encoding=UTF-8
spring.mail.host=smtpout.ohsu.edu
spring.mail.port=25
spring.mail.protocol=smtp
spring.mail.test-connection=false
spring.mail.username=octrihlp@ohsu.edu
spring.mail.password=secret
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=false
```

## Default Behavior

The library enables both LDAP and table based authentication by default. One or the other can be toggled off using these application properties, but at least one must be enabled:

```
octri.authentication.enable-ldap=false
octri.authentication.enable-table-based=true
```

Users will be locked out after 7 login attempts. This limit can be configured as well:

```
octri.authentication.max-login-attempts=3
```

The default is to set the credentials expiration date 180 days in the future when changing a password. To override this, set the following property:

```
octri.authentication.credentials-expiration-period=180
```

User email is required by default, but can be made optional by using the spring profile `noemail`. See no email mode section below for details.

### No email mode

For applications that don't require an email address, do the following to configure your application.

Set the spring profile `noemail`. You can do this a few ways. The easiest is to define the following property in your application. This activates the profile by default. Other profiles can still be activated by setting `spring.profiles.active`.

```
spring.profiles.include=noemail
```

Lastly copy the migration [`V20190621120000__alter_user.sql`](src/main/resources/noemail/db/migration/V20190621120000__alter_user.sql) into your application's Flyway migration directory. Usually `src/main/resources/db/migration`.

### Session Timeout

Session timeout is managed by the three properties shown below. See the [full list of properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html). Also see [OHSU's guidelines](http://ozone.ohsu.edu/cc/sec/isp/00005.pdf).

To override any of these values set the corresponding environment variable (e.g. `server.session.cookie.max-age` becomes `SERVER_SESSION_COOKIE_MAX_AGE`), or define them in your own `application.properties` file. The order in which external configuration is loaded may be found [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

```
# Session timeout in seconds
server.session.timeout=1200

# Cookie max-age in seconds
server.session.cookie.max-age=1200

# Set to true for production when using https - false for http
server.session.cookie.secure=false
```

### Password Requirements

Passwords are validated by the `PasswordConstraintValidator.java` and the `UserController#changePassword()` `POST` method. They follow OHSU password standards. See `ValidPassword.java`.

Users and System Administrators shall employ the following minimum password attributes:

* 8 character minimum
* Inclusion of all of the following elements:
  * An alpha character (e.g., zyxwvut);
  * A numeric character (e.g., 12345);
  * A capitalized letter or punctuation or non-alphanumeric character (e.g., !@#*+)
  * An initial or temporary password that will expire following the first successful login to an account;
  * An account lockout mechanism that is triggered after seven (7) unsuccessful account login attempts (TODO: within thirty (30) minutes).
  * Avoid words found in any dictionary (including medical, foreign language) (TODO: provide more words)
  * Shall not contain the user login-name (UserID).

### User Roles

The library sets up roles USER, ADMIN, and SUPER. The [`UserController`](src/main/java/org/octri/authentication/controller/UserController.java) restricts user management to the admin and super roles, and users cannot edit themselves.

#### Add Roles and Users

For default OCTRI users see the [auth_default_users](https://octriinternal.ohsu.edu/stash/projects/OC/repos/auth_default_users/browse) project. It provides scripts for creating roles and users.

The authentication_lib provides the default roles via Flyway. Those roles are: `ROLE_USER`, `ROLE_ADMIN`, and `ROLE_SUPER`.

If you want roles and users other than what's in the auth_default_users project follow this process to add roles, and users with roles.

```
-- Add a role
INSERT INTO user_role (id, description, role_name)
VALUES (4, 'Manager', 'ROLE_MANAGER');

-- Add a user
INSERT INTO user (account_expiration_date, account_expired, account_locked,
				  consecutive_login_failures, credentials_expiration_date,
				  credentials_expired, email, enabled, first_name, institution, last_name,
				  password, username)
VALUES
	(NULL, 0, 0, 0, NULL, 0, 'foobar@example.com', 1, 'Foo', 'OHSU', 'Bar', NULL, 'foobar');
SET @user_id = (SELECT last_insert_id());

-- Link the role and user
INSERT INTO user_user_role (user, user_role)
VALUES
	(@user_id, 4);
```

If adding a table-based user, you can add an empty or bogus password through SQL and then use the forgot password functionality to get a legitimate hashed password.

### Web Application Authentication and UI

Authentication flow uses fairly standard redirection and provides success and failure handlers to record login attempts and lock accounts after consecutive failures. You may also override the default public routes.

The following methods are provided by the [`BaseSecurityConfiguration`](src/main/java/org/octri/authentication/BaseSecurityConfiguration.java) and can be overridden by the application's security configuration:

* `defaultSuccessUrl()` - Where to redirect after successful login. By default `/admin/user/list`.
* `loginFailureRedirectUrl()` - Where to redirect after failed login. By default `/login?error`
* `logoutUrl()` - The request mapping for logout. By default `/logout`.
* `logoutSuccessUrl()` - Where to redirect after successful logout. By default `/login`.
* `customPublicRoutes()` - Include additional public routes. The default set may be found in the string array: `BaseSecurityConfiguration.DEFAULT_PUBLIC_ROUTES`

For UI, the library provides a login page and navigation bar with links to "Home", User Administration pages, and logout. Review and run the auth_example_project for this most basic setup. User Administration pages are also available as fragments so your application can provide its own navigation or layout. Your application can override any views or request mappings by adding your own versions to your project. For example, if you want your own login page, create `login.html` in the `src/main/resources/templates` directory.

#### Webjars (CSS and JavaScript dependencies)

The authentication library uses Bootstrap 4, Font Awesome, jQuery, jQuery-UI, and DataTables libraries for styling and functionality. These are included as resources through webjars in the `pom.xml` file. The library also uses the webjars-locator dependency to manage versions of the webjars so that your application doesn't have to. To keep in sync with the authentication library, it is recommended that you do not include your own dependencies of these jars but rely on the library to keep them up to date. You can refer to any of the assets provided by the authentication library in your application code. Here is what is included:

CSS is located in the `authlib_fragments/css.mustache` template. By default it includes the following files:

```
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/webjars/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/webjars/font-awesome/css/all.min.css" />
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/css/default.css" />
```

If you provide the `formView` model property it will also include:

```
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/webjars/jquery-ui/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/webjars/jquery-ui/jquery-ui.theme.min.css" />
```

If you provide the `listView` model property it will include:

```
<link rel="stylesheet" type="text/css" href="{{req.contextPath}}/webjars/datatables/css/dataTables.bootstrap4.min.css" />
```

Likewise, JavaScript is included in the `authlib_fragments/assets.mustache` template. By default it includes the following:

```
<script type="text/javascript" src="{{req.contextPath}}/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript" src="{{req.contextPath}}/webjars/bootstrap/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="{{req.contextPath}}/js/default.js"></script>
```

Similar to `css.mustache`, if you pass the `formView` model property it will include the corresponding JavaScript:

```
<script type="text/javascript" src="{{req.contextPath}}/webjars/jquery-ui/jquery-ui.min.js"></script>
```

And if you pass the `listView` model property it will include:

```
<script type="text/javascript" src="{{req.contextPath}}/webjars/datatables/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="{{req.contextPath}}/webjars/datatables/js/dataTables.bootstrap4.min.js"></script>
```

If your application has its own navigation and is using the User Management fragments instead of the templates, you will need to make sure the css and js are loaded properly. You can assume that all pages will need jquery and bootstrap. Other dependencies may not be needed on every page, but the New and Edit User forms, for example, need jquery-ui to show calendar popups and the User list page will need datatables. The `UserController.java` is responsible for setting the `formView` and `listView` model properties for User Management.

### API Authentication

This functionality has not yet been tested or used in an example project, but should provide JSON responses to authentication requests.

## How to use Mustache fragments in your application

The authentication library provides these pages: `admin/user/form.mustache`, `admin/user/list.mustache`, `error.mustache`, `login.mustache`, `user/password/change.mustache`, `user/password/forgot.mustache`, and `user/password/reset.mustache`. You'll likely need to implement these in your own project if you use a custom layout and navbar.

See above in the "Webjars" section for how to include the required CSS and JavaScript. For all pages assume Bootstrap 4, Font Awesome, and jQuery 3 are required.

Create `mustache-templates/login.mustache` and in the body include the fragment: `{{>authlib_fragments/login}}`.

Create `mustache-templates/error.mustache` and in the body include the following HTML. The `UserController` will populate the model properties.

```
<div class="container-fluid">
	<div class="alert alert-danger">
		<p>Error {{status}}: {{error}} ({{message}})</p>
		<p>{{timestamp}}</p>
	</div>
</div>
```

Create `mustache-templates/admin/user/form.mustache` and in the body include the fragment: `{{>authlib_fragments/admin/user/form}}`. This is the **New User** form - link to `{{contextPath}}/admin/user/form`. You can include the required JavaScript by using the fragment `{{>authlib_fragments/assets}}`. Include the required CSS by using the fragment `{{>authlib_fragments/css}}`.

Create `mustache-templates/admin/user/list.mustache` and in the body include the fragment: `{{>authlib_fragments/admin/user/list}}`. This is the **List of Users** page - link to `{{contextPath}}/admin/user/list`. You can include the required JavaScript by using the fragment `{{>authlib_fragments/assets}}`. Include the required CSS by using the fragment `{{>authlib_fragments/css}}`.

**If you want table-based authentication then you need to create three more templates.**

Create `mustache-templates/user/password/change.mustache` and in the body include the fragment: `{{>authlib_fragments/user/password/change}}`.

Create `mustache-templates/user/password/forgot.mustache` and in the body include the fragment: `{{>authlib_fragments/user/password/forgot}}`.

Create `mustache-templates/user/password/reset.mustache` and in the body include the fragment: `{{>authlib_fragments/user/password/reset}}`.

## CSRF

All forms must include a CSRF token. Inside each form include the following fragment: `{{>authlib_fragments/forms/csrf_input}}`
