# User Role Configuration

The OCTRI AuthLib provides support for authorization using role-based access control (RBAC), where the roles granted to a user account determine what actions that user may take. This document describes the internals of AuthLib's RBAC implementation and the configuration parameters that may be used to customize the behavior for your application.

For information on more thorough customization of the user management interface, see [USER_MANAGEMENT_CUSTOMIZATION.md](USER_MANAGEMENT_CUSTOMIZATION.md).

## Architecture

A user role is a [Spring Security `GrantedAuthority`](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html) that follows the naming convention `ROLE_*`. In AuthLib, application roles are represented by [the `UserRole` entity](../authentication_lib/src/main/java/org/octri/authentication/server/security/entity/UserRole.java) and stored in the `user_role` database table. Role grants are represented by [the `UserUserRole` entity](../authentication_lib/src/main/java/org/octri/authentication/server/security/entity/UserUserRole.java) and stored in the `user_user_role` join table. At login, the user's roles are looked up from the database, converted to `GrantedAuthority` objects, and stored in the `SecurityContext` for use in authorization decisions.

This architecture supports different role-based permission models, including:

* Users have a single role. Roles are mutually exclusive.
* Users have one or more roles, and roles are additive.

However, these different permission models require different user interfaces. The former requires a control that only allows selecting a single role (radio buttons or a select list), while the latter should allow multiple selections (checkboxes or a multi-select). For this reason, AuthLib allows configuring a role style.

## Role Style Configuration

The user interface for role selection is configured using the `octri.authentication.role-style` property. This property has three possible values.

| Property value | Consequences |
| - | - |
| `single` | Users may only have a single role. A radio group is used to select the user's role. |
| `multiple` | Users must have one role, but they may have more. A checkbox list is used to select the user's role(s). |
| `custom` | Custom behavior. The application must provide a template for the role selection UI. |

When the `custom` role style is selected, the application must provide a custom Mustache template for the role selection UI. The template must be named `authlib_fragments/admin/user/roles/custom.mustache`. The following useful variables are exposed to the template.

| Name | Type  | Notes  |
| - | - | - |
| newUser | Boolean | True if a new user is being created, false when editing an existing user. |
| user | [User](../authentication_lib/src/main/java/org/octri/authentication/server/security/entity/User.java) | The user being modified. |
| userRoles | [List<EntitySelectOption<UserRole>>](../authentication_lib/src/main/java/org/octri/authentication/) | List of objects with label, value, and selected properties. |


When the `custom` role style is selected, the application may optionally provide JavaScript to validate the selected roles. To do so, create the JavaScript file, then set the `octri.authentication.custom-role-script` property to the file path, relative to the application's context path. For example:

```properties
octri.authentication.custom-script-path=assets/js/example.js
```
