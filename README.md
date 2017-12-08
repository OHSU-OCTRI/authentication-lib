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

The library will transitively bring in several Spring Boot jars along with MySQL, Flyway, etc. In your Application definition, the Spring Boot Runner needs to set some additional parameters to ensure that domain, repositories, and autowired components for the Authentication Library are picked up:

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
ldap.contextSource.url=${LDAP_CONTEXTSOURCE_URL}
ldap.contextSource.userDn=${LDAP_CONTEXTSOURCE_USERDN}
ldap.contextSource.password=${LDAP_CONTEXTSOURCE_PASSWORD}
ldap.contextSource.searchBase=${LDAP_CONTEXTSOURCE_SEARCHBASE}
ldap.contextSource.searchFilter=${LDAP_CONTEXTSOURCE_SEARCHFILTER}
ldap.contextSource.organization=${LDAP_CONTEXTSOURCE_ORGANIZATION}
```

If using the standard Docker/MySQL setup, start the MySQL container first and create the database and user. Then start up your application and Flyway migrations for the authentication library should create some structure for users and roles.

## Default Behavior

The library enables both LDAP and table based authentication by default. One or the other can be toggled off using these application properties:

```
octri.authentication.enable-ldap=false
octri.authentication.enable-table-based=true
```

Users will be locked out after 3 login attempts. This limit can be configured as well:

```
octri.authentication.max-login-attempts=5
```

### User Roles

The library sets up roles USER, ADMIN, and SUPER. The [`UserController`](src/main/java/org/octri/authentication/controller/UserController.java) restricts user management to the admin and super roles, and users cannot edit themselves.

#### Add Roles and Users

The authentication library provides a sample [SQL script](setup/sql/add_roles_and_users.sql) for adding roles, and users with roles.

You may execute this script manually,

```
mysql -u foo -p -h 127.0.0.1 my_db < setup/sql/add_roles_and_users.sql
```

or by running the [helper script](setup/add_roles_and_users.sh) that will prompt for connection details,

```
./setup/add_roles_and_users.sh
```

### Web Application Authentication and UI

Authentication flow uses fairly standard redirection and provides success and failure handlers to record login attempts and lock accounts after consecutive failures.

The following methods are provided by the [`BaseSecurityConfiguration`](src/main/java/org/octri/authentication/BaseSecurityConfiguration.java) and can be overridden by the application's security configuration:

* `defaultSuccessUrl()` - Where to redirect after successful login. By default `/admin/user/list`.
* `loginFailureRedirectUrl()` - Where to redirect after failed login. By default `/login?error`
* `logoutUrl()` - The request mapping for logout. By default `/logout`.
* `logoutSuccessUrl()` - Where to redirect after successful logout. By default `/login`.

For UI, the library provides a login page and navigation bar with links to "Home", User Administration pages, and logout. Review and run the auth_example_project for this most basic setup. User Administration pages are also available as fragments so your application can provide its own navigation or layout. Your application can override any views or request mappings by adding your own versions to your project. For example, if you want your own login page, create `login.html` in the `src/main/resources/templates` directory.

#### Webjars

The authentication library uses bootstrap, jquery, and datatables libraries for styling and functionality. These are included as resources through webjars in the pom.xml file. The library also uses the webjars-locator dependency to manage versions of the webjars so that your application doesn't have to. To keep in sync with the authentication library, it is recommended that you do not include your own dependencies of these jars but rely on the library to keep them up to date. You can refer to any of the assets provided by the authentication library in your application code. Here is what is included:

CSS:
```
<link rel="stylesheet" type="text/css" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}" />
<link rel="stylesheet" type="text/css" th:href="@{/webjars/datatables/media/css/jquery.dataTables.min.css}" />
<link rel="stylesheet" type="text/css" th:href="@{/webjars/datatables/media/css/dataTables.bootstrap.min.css}" />
<link rel="stylesheet" type="text/css" th:href="@{/webjars/jquery-ui/jquery-ui.min.css}" />
<link rel="stylesheet" type="text/css" th:href="@{/webjars/jquery-ui/jquery-ui.theme.min.css}" />
<link rel="stylesheet" type="text/css" th:href="@{/css/default.css}" />
```
Javascript:
```
<script type="text/javascript" th:src="@{/webjars/datatables/media/js/jquery.js}"></script>
<script type="text/javascript" th:src="@{/webjars/jquery-ui/jquery-ui.min.js}"></script>
<script type="text/javascript" th:src="@{/webjars/bootstrap/js/bootstrap.min.js}" />
<script type="text/javascript" th:src="@{/webjars/datatables/media/js/jquery.dataTables.min.js}"></script>
<script type="text/javascript" th:src="@{/webjars/datatables/media/js/dataTables.bootstrap.min.js}"></script>
<script type="text/javascript" th:src="@{/js/default.js}"></script>
```

If your application has its own navigation and is using the User Management fragments instead of the templates, you will need to make sure the css and js are loaded properly. You can assume that all pages will need jquery and bootstrap. Other dependencies may not be needed on every page, but the New and Edit User forms, for example, need jquery-ui to show calendar popups and the User list page will need datatables.

TODO: Refactor so it is clear what each page needs instead of having all css loaded and the assets fragment loading all js on all pages.

### API Authentication

This functionality has not yet been tested or used in an example project, but should provide JSON responses to authentication requests.

