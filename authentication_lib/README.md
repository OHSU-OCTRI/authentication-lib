# Authentication Library Core

This package contains the core logic of the authentication library, including:

* Domain entities
* Support for LDAP and table-based login
* Auditing of login attempts
* Support for password validation rules
* Default controllers for user management
* Default URL security rules

Default user interface templates for login and user management styled using Bootstrap 4 and Bootstrap 5 are provided by separate packages.

* [Authentication UI for Bootstrap 4](../authentication_ui_bootstrap4/)
* [Authentication UI for Bootstrap 5](../authentication_ui_bootstrap5/)

## Using This Package

To use this package, add it to your `pom.xml` file, along with one of the UI packages. The following example uses the Bootstrap 5 UI.

```xml
	<dependency>
		<groupId>org.octri.authentication</groupId>
		<artifactId>authentication_lib</artifactId>
		<version>${authentication_lib.version}</version>
	</dependency>
	<dependency>
		<groupId>org.octri.authentication</groupId>
		<artifactId>authentication_ui_bootstrap4</artifactId>
		<version>${authentication_lib.version}</version>
	</dependency>
```

## Implementation

The library is implemented using [Spring Boot](https://spring.io/projects/spring-boot) and [Spring Security](https://spring.io/projects/spring-security). For a detailed list of dependencies, see [pom.xml](./pom.xml).
