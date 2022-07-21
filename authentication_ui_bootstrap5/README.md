# Authentication UI for Bootstrap 5

This package contains Mustache templates and static assets required by the authentication library, styled using Bootstrap 5.

## Using This Package

To render AuthLib views with Bootstrap 5 markup, install this package alongside the `authentication_lib` package.

```xml
	<dependency>
		<groupId>org.octri.authentication</groupId>
		<artifactId>authentication_lib</artifactId>
		<version>${authentication_lib.version}</version>
	</dependency>
	<dependency>
		<groupId>org.octri.authentication</groupId>
		<artifactId>authentication_ui_bootstrap5</artifactId>
		<version>${authentication_lib.version}</version>
	</dependency>
```

## Implementation

### Dependencies

This package depends on `spring-boot-starter-mustache` and WebJars for Bootstrap 5, jQuery 3, jQuery UI, DataTables, and Font Awesome. These packages should be provided by your application or transitively via `authentication_lib`. See [`pom.xml`](./pom.xml) for details.

### Templates and Assets

Mustache templates are stored in [`src/main/resources/mustache-templates`](./src/main/resources/mustache-templates/), as expected by the Mustache configuration provided by the `authentication_lib` package. All of the templates used by `authenticaton_lib` are provided; if for some reason a template is missing [`authentication_lib`'s template configuration class](../authentication_lib/src/main/java/org/octri/authentication/config/TemplateConfiguration.java) will log warnings at startup.

Some templates in the user management interface use custom styles and JavaScript for a richer user experience. These assets are found in [`src/main/resources/assets/static/assets`](./src/main/resources/static/assets/).
