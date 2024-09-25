# User Management Customization

The OCTRI AuthLib provides a default user management workflow that works for simple cases. For use cases where the default behavior is insufficient, hooks provided by the [`UserManagementCustomizer`][customizer_interface] interface may be used to override the default behavior. This document describes the [`UserManagementCustomizer`][customizer_interface] interface, the default behavior implemented by [`DefaultUserManagementCustomizer`][default_customizer], and how to override the default behavior.

## Default Behavior

If a [`UserManagementCustomizer`][customizer_interface] bean is not found in the application container at startup, AuthLib will provide an instance of the [`DefaultUserManagementCustomizer`][default_customizer] class, which will provide the default user management workflow. The default workflow varies slightly, depending on whether a user account is being created or updated.

### On Create

This is the default workflow when creating a new user account.

1. A user with administrative rights completes and submits the user form.
2. Form data is submitted to the [`UserController`][user_controller].
3. The new account is saved.
4. If the new account uses table-based authentication, a welcome message with an initial password setup link is sent to the account's associated email address.
5. The user is redirected back to the user list.

The following configuration properties can be used to tweak aspects of the default workflow:

* `octri.authentication.email-dry-run`
* `octri.authentication.email-required`
* `octri.authentication.password-token-valid-for`
* `octri.authentication.role-style`
* `octri.authentication.custom-role-script`

See [`CONFIGURATION_PROPERTIES.md`](./CONFIGURATION_PROPERTIES.md) for details.

### On Update

The default workflow for updating a user account is the same, except the welcome email is not sent.

1. A user with administrative rights updates properties of an existing user and submits the user form.
2. Form data is submitted to the [`UserController`][user_controller].
3. The account changes are saved.
5. The user is redirected back to the user list.

## Overriding the Default Workflow

To customize the user management workflow for your application's needs, provide a bean that implements the [`UserManagementCustomizer`][customizer_interface] interface.

```java
@Configuration
public class MyCustomConfig {

    @Bean
    public UserManagementCustomizer applicationUserManagementCustomizer() {
      return new MyApplicationUserManagementCustomizer(/* ... */);
    }

}
```

Because the [`DefaultUserManagementCustomizer`][default_customizer] implements the [`UserManagementCustomizer`][customizer_interface] interface, you may want to extend it or use it as an example when implementing your own customizer.

### Customizer Methods

The [`UserManagementCustomizer`][customizer_interface] interface provides hooks for custom logic after user accounts are created, after user accounts are updated, and before user accounts are saved (create or update). When called, each hook is provided the user entity being acted upon and the form submission request, allowing implementers to dispatch to custom logic based on properties of the user. The hook then returns a `ModelAndView` object that is used to render the response.

All methods have a default implementation, so you only need to implement the methods you want to override.

#### postCreateAction

This method is called after a new user account has been persisted. By default, it returns a `ModelAndView` that redirects back to the user list page.

Example uses:

* Auditing account creation
* Sending new account notifications
* Creating additional records
* Rendering a form for other account attributes or associations

#### postUpdateAction

This method is called after changes to an existing user account have been persisted. By default, it returns a `ModelAndView` that redirects back to the user list page.

Example uses:

* Auditing account modification
* Sending notifications
* Creating, updating, or deleting other records
* Rendering a form for other account attributes or associations

#### beforeSaveAction

This method is called just before saving the given user entity, which may represent a new user or an existing user. The method may intercept the save by returning an optional `ModelAndView`. By default `Optional.empty()` is returned, and the user account is saved.

Example uses:

* Application-specific user validation
* Rendering a form for other account attributes or associations

## References

[customizer_interface]: ../authentication_lib/src/main/java/org/octri/authentication/server/customizer/UserManagementCustomizer.java
[default_customizer]: ../authentication_lib/src/main/java/org/octri/authentication/server/customizer/DefaultUserManagementCustomizer.java
[user_controller]: ../authentication_lib/src/main/java/org/octri/authentication/server/controller/UserController.java