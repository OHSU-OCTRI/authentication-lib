# Password Visibility Toggle

The OCTRI AuthLib provides a password visibility toggle button on password input fields. When enabled, a toggle button appears alongside each password input, allowing users to reveal or hide the password they have typed. This feature is enabled by default and can be disabled via a configuration property.

## Configuration

To enable the password visibility toggle, set the `octri.authentication.enable-password-visibility-toggle` property to `true`. This feature is enabled by default.

## Architecture

When `octri.authentication.enable-password-visibility-toggle` is `true`, password inputs in the built-in templates are marked with a `data-password-toggle` attribute. At page load, [`password-visibility.js`](../authentication_ui_bootstrap5/src/main/resources/static/assets/js/password-visibility.js) scans the page for all inputs with that attribute and inserts the toggle button. When the form is submitted, all toggled inputs are reset to type `password` to ensure that browsers do not offer to autocomplete the field. For accessibility, the toggle button includes a visually hidden `aria-live` region that announces the current password visibility state to screen readers.

The toggle button is styled by the `.show-password-toggle` CSS class defined in [`authlib.css`](../authentication_ui_bootstrap5/src/main/resources/static/assets/css/authlib.css).

The `password-visibility.js` script and `authlib.css` stylesheet are automatically included in the page if your application is inserting the following mustache fragments:

* [authlib_fragments/assets.mustache](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/authlib_fragments/assets.mustache)
* [authlib_fragments/css.mustache](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/authlib_fragments/css.mustache)

## Custom Templates

If your application overrides any of the built-in password form templates and wants to preserve toggle functionality, add the `data-password-toggle` attribute to any `<input type="password">` element, conditional on the `enablePasswordVisibilityToggle` Mustache variable. For example:

```html
<input type="password" class="form-control" {{#enablePasswordVisibilityToggle}}data-password-toggle{{/enablePasswordVisibilityToggle}}>
```

The `enablePasswordVisibilityToggle` variable is added to every template model by [`TemplateAdvice`](../authentication_lib/src/main/java/org/octri/authentication/server/controller/TemplateAdvice.java) and reflects the value of the `octri.authentication.enable-password-visibility-toggle` property.

This can also be used to add the behavior to any other password inputs in your application.

## Classes and Files

* [`OctriAuthenticationProperties`](../authentication_lib/src/main/java/org/octri/authentication/config/OctriAuthenticationProperties.java) — configuration properties bean; exposes the `enablePasswordVisibilityToggle` flag
* [`TemplateAdvice`](../authentication_lib/src/main/java/org/octri/authentication/server/controller/TemplateAdvice.java) — adds `enablePasswordVisibilityToggle` as a model attribute available to all Mustache templates
* [`assets.mustache`](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/authlib_fragments/assets.mustache) — loads `password-visibility.js` on every page
* [`login_form.mustache`](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/authlib_fragments/login_form.mustache) — applies the toggle to the login page password input
* [`user/password/form.mustache`](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/authlib_fragments/user/password/form.mustache) — applies the toggle to the current password, new password, and confirm password inputs on the password change form
* [`password-visibility.js`](../authentication_ui_bootstrap5/src/main/resources/static/assets/js/password-visibility.js) — initializes toggle buttons for all `input[data-password-toggle]` elements on the page
* [`authlib.css`](../authentication_ui_bootstrap5/src/main/resources/static/assets/css/authlib.css) — styles the toggle button via the `.show-password-toggle` class

## References

* [Simple things are complicated: making a show password option (gov.uk)](https://technology.blog.gov.uk/2021/04/19/simple-things-are-complicated-making-a-show-password-option/)
* [Dos and don’ts of accessible show password buttons (medium.com)](https://medium.com/@web-accessibility-education/dos-and-donts-of-accessible-show-password-buttons-9a5fbc2c566b)
