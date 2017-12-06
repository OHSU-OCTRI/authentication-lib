/**
 * Redirects to the value set in the hidden input field #contextPath
 */
function cancel() {
	location.href = $('#contextPath').val();
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
 * Produces muted text div with the provided text.
 * @param text The text to display.
 * @param classNames A space separated list of CSS class names.
 * @returns HTML div with given text and classes.
 */
function mutedDiv(text, classNames) {
	return '<div class="text-muted ' + classNames + '">' + text + '</div>';
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
	$('.users-table').DataTable({
		responsive: true,
		columnDefs: [{
			targets: 0,
			orderable: false
		}],
		order: [[ 1, "asc" ]],
		dom: 'fltip' /* Switch default ordering of table elements so search filter is before length selector */
	});
	
	$('.btn.cancel').on('click', cancel);
	
	$("input[name=accountExpirationDate]").datepicker();
	$("input[name=credentialsExpirationDate]").datepicker();
	
	let url = $("meta[name='ctx']").attr("content") + "admin/user/taken";
	
	$('#username').on('keydown blur change', debounce(function() {
		let username = $('#username').val();
		if (username != null && username != '') {
			$.get(url + '/' + encodeURIComponent(username), function(json) {
				$('.username-taken').remove();
				if (json.taken) {
					disableSave(true);
					if ($('.username-taken').size() === 0) {
						$('#username').after(mutedDiv('This username is taken.', 'username-taken'));
					}
				} else {
					disableSave(false);
					if ($('.username-taken').size() === 0) {
						$('#username').after(mutedDiv('Username available.', 'username-taken'));
					}
				}
			});
		} else {
			$('.username-taken').remove();
		}
	}, 500));
	
	$('#ldapLookup').on('click', function(e) {
		e.preventDefault();
		let token =  $('input[name="_csrf"]').attr('value');
	    $.ajaxSetup({
	        beforeSend: function(xhr) {
	            xhr.setRequestHeader('X-CSRF-TOKEN', token);
	        }
	    });
	    
		let username = $('#username').val();
		let obj = {username: username};

		$.post( "/admin/user/ldapLookup", obj, function(json) {
			if (json.ldapLookupError) {
				// TODO: Make this better
				$('#username').after(mutedDiv(json.ldapLookupError, ''));
			}
			$('#firstName').val(json.firstName);
			$('#lastName').val(json.lastName);
			$('#email').val(json.email);
			$('#institution').val(json.institution);
		});
	});

});