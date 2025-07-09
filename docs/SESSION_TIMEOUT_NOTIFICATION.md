# Session Timeout Notification

We include support for notifying users when their session is about to expire. When enabled, a modal dialog is displayed when the user's session is about to expire, with the option to stay logged in. If the user does not interact with the dialog before the session times out, they are automatically logged out.

## Basic Usage

To use this feature with the default behavior, add a meta tag to your page header to expose the `sessionTimeoutSeconds` template attribute.

```mustache
	{{#sessionTimeoutSeconds}}
	<meta name="session-timeout-seconds" content="{{.}}">
	{{/sessionTimeoutSeconds}}
```

This can also be done by adding the following fragment to the page header.

```mustache
{{>authlib_fragments/meta_tags}}
```

Next, include the following fragment somewhere in the footer of your application.

```mustache
{{>authlib_fragments/default_session_timeout_modal}}
```

This will add the needed markup and JavaScript to the page to alert authenticated users two minutes before their session expires.

## Advanced Usage

As noted above, by default the session timeout notification is only shown to authenticated users. If you need to customize the logic that decides when the modal is added to the page (e.g. only for users with a specific role), you can include the `session_timeout_modal` fragment. You can guard this using any attribute of the template model.

```mustache
{{#isMySpecialUserType}}
{{>authlib_fragments/session_timeout_modal}}
<script type="text/javascript" src="{{req.contextPath}}/assets/js/install-session-timeout.js"></script>
{{/isMySpecialUserType}}
```

You can customize the behavior of the modal dialog using custom JavaScript. The modal's constructor accepts an `options` object that allows configuring many aspects of the modal's behavior, including:

* How long the code waits before displaying the modal
* How long after displaying the modal will the user be logged out
* The endpoint to use to keep the session alive
* The logout endpoint
* Callback to execute before logging the user out

See [octri-session-timeout-modal.js](../authentication_ui_bootstrap5/src/main/resources/static/assets/js/octri-session-timeout-modal.js) and [install-session-timeout.js](../authentication_ui_bootstrap5/src/main/resources/static/assets/js/install-session-timeout.js) for details.
