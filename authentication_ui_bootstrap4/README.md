# Authentication UI Templates for Bootstrap 4

Development Info
================

* [Wiki](https://octri.ohsu.edu/wiki/display/ENTER_CONFLUENCE_KEY/ENTER_PAGE_NAME)
* [Issues](https://octri.ohsu.edu/issues/projects/ENTER_JIRA_KEY/issues/)

This is a [Spring Boot](https://projects.spring.io/spring-boot/) project. It uses a mysql database for storage, managed using Flyway.

Setup
=====

Configuration
-------------

Copy `env.sample` to `.env` and update as needed.

No changes required unless you want different database passwords.

Application Startup
-------------------
**Running with Docker**


Build the project,

```
mvn clean package -DskipTests
```

Start the containers, getting mysql up first

```
docker-compose up -d mysql
docker-compose up -d app
```

You should find the app at,

http://localhost:8080/authentication_ui_boostrap4

unless you updated `SERVER_SERVLET_CONTEXTPATH`.

**Running with Eclipse**

See [Running Chimera Applications with Eclipse](https://octri.ohsu.edu/wiki/display/ORDS/Running+Chimera+Applications+with+Eclipse) for a complete guide.


Templates
---------

Mustache templates are stored in `src/main/resources/mustache-templates` which was overridden in [`src/main/resources/application.properties`](src/main/resource/application.properties) by the property `spring.mustache.prefix`.

By default there is a `home.mustache` template that uses a header layout (`layout/header.mustache`) and a footer layout (`layout/footer.mustache`).

Bootstrap 4 and jQuery 3 are both included in the templates. Additional CSS styles may be added to `static/css/main.css`.

Integration tests requiring a database
--------------------------------------

To bring up a testing database you may use the Docker Compose file `docker-compose.test.yml`:

```
docker-compose -f docker-compose.test.yml up -d
```

This brings up a second MySQL database container on port 3307. `test-application.properties` overrides the datasource URL.

Add the following annotations to your test class which will bring up a full application context that uses this test datasource.

```
@RunWith(SpringRunner.class)
@TestPropertySource(locations = { "classpath:application.properties", "classpath:test-application.properties" })
@SpringBootTest
```

Flyway Migrations
-----------------

To create a Flyway migration, create a version directory in `src/main/resources/db/migration`. For example:

```
mkdir src/main/resources/db/migration/0.0.1
```

Now add your migrations in this directory. For example, `V19700101000042__my_first_migration.sql` which follows the format: `V`, followed by the year, month, day, hours, minutes, seconds (YYYYMMDDhhmmss), two underscores, a short description, and finally `.sql`.
