/**
 * Redirects to the value set in the hidden input field #contextPath
 */
function cancel() {
	location.href = $("meta[name='ctx']").attr("content");
}

/**
 * Redirects to the user list form after canceling a user edit
 */
function cancelUserForm() {
	location.href = $("meta[name='ctx']").attr("content") + "admin/user/list";
}

/**
 * Use debounce to wait for keyboard silence before executing a function.
 * For example:
 * 	 $('#username').on('keydown blur change', debounce(myfunction));
 * @see https://john-dugan.com/javascript-debounce/
 * @param func
 * @param wait
 * @param immediate
 * @returns
 */
function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
}

/**
 * Produces a div with the provided classes.
 * @param text The text to display.
 * @param classNames A space separated list of CSS class names.
 * @returns HTML div with given text and classes.
 */
function div(text, classNames) {
	return '<div class="' + classNames + '">' + text + '</div>';
}

/**
 * Enable or disable save button on save and cancel form fragment.
 */
function disableSave(booleanValue) {
	if (booleanValue) {
		$('.form-controls button[name=save]').attr('disabled', 'disabled');
	} else {
		$('.form-controls button[name=save]').attr('disabled', null);
	}
}

$(function() {
	if (typeof $.fn.DataTable !== 'undefined') {
		$('.authlib-user-list .users-table').DataTable({
			columnDefs: [{
				targets: 0,
				orderable: false
			}],
			order: [[ 1, "asc" ]]
		});
	}

	$('.btn.cancel').on('click', cancel);
	$('.btn.cancelUserForm').on('click', cancelUserForm);

	if (typeof $.fn.datepicker !== 'undefined') {
		$("input[name=accountExpirationDate]").datepicker();
		$("input[name=credentialsExpirationDate]").datepicker();
	}

	$('[data-action="popover"]').popover({
		trigger: "click hover",
		html: true
	});

	var contextPath = $("meta[name='ctx']").attr("content");

	/**
	 * Username type-ahead lookup
	 * <input id="username" class="lookup-user" />
	 * Add the `lookup-user` class and after typing a username a lookup
	 * will be made. A div will be appended with a message about the username.
	 */
	$('#username.lookup-user').on('keydown blur change', debounce(function() {
		var username = $('#username').val();
		if (username !== null && typeof username !== 'undefined' && username !== '') {
			// prevent xss - usernames must be lowercase
			var filteredUsername = username.toLowerCase().replace(/[^a-z0-9]/g, '');
			// replace username input with filtered username
			$('#username').val(filteredUsername);
			$.get(contextPath + 'admin/user/taken/' + encodeURIComponent(filteredUsername), function(json) {
				$('.username-taken').remove();
				if (json.taken) {
					disableSave(true);
					if ($('.username-taken').length === 0) {
						$('#username').after(div(filteredUsername + ' is taken', 'username-taken mark bg-danger text-light'));
					}
				} else {
					disableSave(false);
					if ($('.username-taken').length === 0) {
						$('#username').after(div(filteredUsername + ' is available', 'username-taken mark bg-success text-light'));
					}
				}
			});
		} else {
			$('.username-taken').remove();
		}
	}, 500));

	// Enable/disable LDAP Lookup based on whether ldapUser exists and is checked
	var ldapNotChecked = $('#ldap_user').length > 0 && !$('#ldap_user').is(':checked');
	$('#ldap_lookup').prop("disabled", ldapNotChecked);

	$('#ldap_user').on('click', function(e) {
		$('#ldap_lookup').prop("disabled", !$(this).is(':checked'));
	});

	// Look up by username in LDAP and prepopulate user fields
	$('#ldap_lookup').on('click', function(e) {
		e.preventDefault();
		var token =  $('input[name="_csrf"]').attr('value');
	    $.ajaxSetup({
	        beforeSend: function(xhr) {
	            xhr.setRequestHeader('X-CSRF-TOKEN', token);
	        }
	    });

		var username = $('#username').val();
		var obj = {username: username};

		$.post(contextPath + "admin/user/ldapLookup", obj, function(json) {
			$('.ldap-error').remove();
			if (json.ldapLookupError) {
				$('#ldap_lookup').before(div(json.ldapLookupError, 'ldap-error text-danger small'));
			} else {
				$('#first_name').val(json.firstName);
				$('#last_name').val(json.lastName);
				$('#email').val(json.email);
				$('#institution').val(json.institution);
			}
		});
	});

	/**
	 * Handle displaying inline form validation errors.
	 * UserController is responsible for sending the BindingResult in the model.
	 * The mustache template renders hidden input fields with the correct keys
	 * and messages which are matched with the form element IDs using this JavaScript.
	 */
	$('[data-error]').each(function(i, el) {
		var message = $(el).data('message');
		$('[name=' + $(el).data('field') + ']')
			.parent('.form-group')
			.addClass('has-error')
			.append('<p class="error-text">' + message + '</p>');
	})

	/**
	 * Automatically focus a form element. Used by login and password reset forms.
	 */
	$('[data-focus]').first().focus();
});