(function () {
  // A string of letters, numbers, hyphens, underscores, periods, or plus characters.
  const usernamePattern = '[A-Za-z0-9.+_\\-]+';

  // A simplified version of the standard email input pattern that excludes exotic email addresses.
  // See https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/email#Basic_validation
  const emailPattern =
    '[a-zA-Z0-9.+_\\-]+@[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*';

  // Allows either a bare username or an email address.
  const mixedPattern = '(?:' + usernamePattern + ')|(?:' + emailPattern + ')';

  /**
   * Delays a callback until after a noisy event stops firing.
   * For example:
   *  someElement.addEventHandler('keydown', debounce(myfunction));
   * @see https://www.joshwcomeau.com/snippets/javascript/debounce/
   * @param callback
   * @param wait
   * @returns
   */
  function debounce(callback, wait) {
    let timeout = null;
    return function () {
      const args = arguments;
      window.clearTimeout(timeout);
      timeout = setTimeout(function () {
        callback.apply(null, args);
      }, wait);
    };
  }

  /**
   * Gets the context path from our standard <meta> tag.
   */
  function getContextPath() {
    const contextPathMeta = document.querySelector('meta[name="ctx"]');
    let contextPath = '';

    if (contextPathMeta) {
      contextPath = contextPathMeta.getAttribute('content');
    }

    if (!contextPath) {
      console.warn(
        'Context path was missing or blank. This will probably cause problems.'
      );
    }

    return contextPath;
  }

  /**
   * Redirects to the root of the application.
   */
  function cancelForm() {
    location.href = getContextPath();
  }

  /**
   * Redirects to the user list.
   */
  function cancelUserForm() {
    location.href = getContextPath() + 'admin/user/list';
  }

  /**
   * Sets the pattern used to validate usernames, based on the configured username style.
   */
  function setUsernamePattern(usernameInput, usernameStyle) {
    if (usernameStyle === 'MIXED') {
      usernameInput.setAttribute('pattern', mixedPattern);
    } else if (usernameStyle === 'EMAIL') {
      usernameInput.setAttribute('pattern', emailPattern);
    } else {
      usernameInput.setAttribute('pattern', usernamePattern);
    }
  }

  /**
   * Returns an appropriate validation error for a username that fails pattern validation.
   */
  function getUsernameValidationError(usernameInput) {
    const characterMessage = 'May only contain the characters a-z0-9.+_-';
    const emailMessage = 'Not a valid email address';

    const value = usernameInput.value;
    const pattern = usernameInput.getAttribute('pattern');
    if (pattern === mixedPattern) {
      return value.includes('@') ? emailMessage : characterMessage;
    } else if (pattern === emailPattern) {
      return emailMessage;
    } else {
      return characterMessage;
    }
  }

  /**
   * Finds the form group containing the given input element.
   */
  function findFormGroupForElement(inputElement) {
    const formGroupClass = 'form-group';
    let parentElement = inputElement.parentElement;
    while (!parentElement.classList.contains(formGroupClass)) {
      if (parentElement.parentElement) {
        parentElement = parent.parentElement;
      }
    }

    if (!parentElement.classList.contains(formGroupClass)) {
      console.error('Element is not enclosed in a form group: ', inputElement);
      return null;
    }

    return parentElement;
  }

  /**
   * Finds the corresponding `.invalid-feedback` element for the given input.
   */
  function findErrorDivForElement(inputElement) {
    const formGroup = findFormGroupForElement(inputElement);

    if (!formGroup) {
      console.error('Element is not enclosed in a form group: ', inputElement);
      return;
    }

    return formGroup.querySelector('.invalid-feedback');
  }

  /**
   * Updates the validation feedback for the username input, based on its current validation state.
   */
  function updateUsernameFeedback(usernameInput) {
    const feedbackElement = findErrorDivForElement(usernameInput);
    if (!feedbackElement) {
      console.error('No feedback element associated with username input');
    }

    const validityState = usernameInput.validity;
    if (!validityState.valid) {
      const errors = [
        validityState.customError ? usernameInput.validationMessage : '',
        validityState.valueMissing ? 'Value must be present' : '',
        validityState.patternMismatch ? getUsernameValidationError(usernameInput) : '',
        validityState.tooLong ? 'Username too long (> 50 characters)' : ''
      ].filter(Boolean);
      feedbackElement.textContent = errors.join(' ');

      usernameInput.classList.remove('is-valid');
      usernameInput.classList.add('is-invalid');
    } else {
      usernameInput.classList.remove('is-invalid');
      usernameInput.classList.add('is-valid');
    }
  }

  /**
   * Validates that email input matches the configured LDAP domain, if the authenticationMethod
   * is set to LDAP.
   * 
   * The browser's built-in checks (required, format, length) take precedence.
   * @returns true if the email is valid
   */
  function validateLdapEmail(emailInput, authenticationMethod, ldapEmailDomain, feedbackElement, defaultMessage) {
    // Clear any previous domain error; empty input is left to the built-in required check
    emailInput.setCustomValidity('');

    const email = emailInput.value.trim().toLowerCase();
    const domainMatches = authenticationMethod !== 'LDAP'
      || email === ''
      || email.endsWith(ldapEmailDomain.toLowerCase());
    let errorMessage = '';
    if (!emailInput.validity.valid) {
      errorMessage = defaultMessage;
    } else if (!domainMatches) {
      errorMessage = 'Email must end with ' + ldapEmailDomain + ' for LDAP accounts';
    }

    if (errorMessage !== '') {
      emailInput.setCustomValidity(errorMessage);
    }
    if (feedbackElement) {
      feedbackElement.textContent = errorMessage;
    }
    return errorMessage === '';
  }

  /**
   * Displays an LDAP search error message.
   */
  function showLdapError(message) {
    const ldapErrorDiv = document.getElementById('ldap_error');
    if (!ldapErrorDiv) {
      console.error('LDAP error message element not found');
      return;
    }

    ldapErrorDiv.textContent = message;
    ldapErrorDiv.classList.remove('invisible');
  }

  /**
   * Hides the LDAP error message.
   */
  function hideLdapError() {
    const ldapErrorDiv = document.getElementById('ldap_error');
    if (!ldapErrorDiv) {
      console.error('LDAP error message element not found');
      return;
    }

    ldapErrorDiv.textContent = '';
    ldapErrorDiv.classList.add('invisible');
  }

  window.addEventListener('load', function () {
    //
    // jQuery plugins
    //

    // DataTables
    if (typeof $.fn.DataTable !== 'undefined') {
      $('.authlib-user-list .users-table').DataTable({
        columnDefs: [
          {
            targets: 0,
            orderable: false
          }
        ],
        order: [[1, 'asc']]
      });
    }

    // Date picker
    if (typeof $.fn.datepicker !== 'undefined') {
      $('input[name=accountExpirationDate]').datepicker();
      $('input[name=credentialsExpirationDate]').datepicker();
    }

    //
    // User form event handlers
    //

    const userForm = document.getElementById('user_form');
    const dataProperties = userForm ? userForm.dataset : {};
    const ldapEmailDomain = dataProperties.ldapEmailDomain
      ? '@' + dataProperties.ldapEmailDomain
      : null;
    const usernameStyle = dataProperties.usernameStyle;

    const cancelButtons = document.querySelectorAll('.btn.cancel');
    for (const cancelButton of cancelButtons) {
      cancelButton.addEventListener('click', cancelForm);
    }

    const userFormCancelButtons = document.querySelectorAll('.btn.cancelUserForm');
    for (const cancelButton of userFormCancelButtons) {
      cancelButton.addEventListener('click', cancelUserForm);
    }

    // Check whether the username is already in use
    const usernameInput = document.getElementById('username');
    if (usernameInput && usernameInput.classList.contains('lookup-user')) {
      setUsernamePattern(usernameInput, usernameStyle);

      const searchHandler = debounce(function (_evt) {
        const searchEndpoint = getContextPath() + 'admin/user/taken/';
        const username = usernameInput.value.toLowerCase();

        usernameInput.value = username;
        usernameInput.setCustomValidity('');
        usernameInput.checkValidity();

        if (!usernameInput.validity.valid) {
          updateUsernameFeedback(usernameInput);
          return;
        }

        if (username) {
          if (usernameStyle === 'MIXED' && username.endsWith(ldapEmailDomain)) {
            usernameInput.setCustomValidity(
              'Username should not be an email for LDAP users.'
            );
            updateUsernameFeedback(usernameInput);
            return;
          }

          fetch(searchEndpoint + encodeURIComponent(username))
            .then(response => {
              if (!response.ok) {
                const errorMessage = 'Search request failed';
                usernameInput.setCustomValidity(errorMessage);
                throw new Error(errorMessage);
              }
              return response.json();
            })
            .then(jsonData => {
              if (jsonData.taken) {
                usernameInput.setCustomValidity('Username is taken');
              } else {
                usernameInput.setCustomValidity('');
              }
            })
            .catch(reason => console.error(reason))
            .finally(() => updateUsernameFeedback(usernameInput));
        }
      }, 500);

      usernameInput.addEventListener('input', searchHandler);
    }

    const authenticationMethodInput = document.getElementById('authentication_method');
    const emailInput = document.getElementById('email');

    // Enable LDAP lookup when appropriate authentication method is selected
    const ldapLookupButton = document.getElementById('ldap_lookup');
    if (ldapLookupButton && authenticationMethodInput) {
      authenticationMethodInput.addEventListener('change', () => {
        ldapLookupButton.disabled = authenticationMethodInput.value !== 'LDAP';
      });
      authenticationMethodInput.dispatchEvent(new Event('change'));

      // Look up by username in LDAP and prepopulate user fields
      ldapLookupButton.addEventListener('click', function (evt) {
        evt.preventDefault();
        hideLdapError();
        const username = usernameInput ? usernameInput.value : null;

        if (!username) {
          return;
        }

        const csrfTokenInput = document.querySelector('input[name="_csrf"]');
        const csrfToken = csrfTokenInput ? csrfTokenInput.value : null;

        const ldapLookupEndpoint = getContextPath() + 'admin/user/ldapLookup';
        const requestBody = new FormData();
        requestBody.set('username', username);

        fetch(ldapLookupEndpoint, {
          method: 'post',
          body: requestBody,
          headers: {
            'X-CSRF-TOKEN': csrfToken
          }
        })
          .then(response => {
            if (!response.ok) {
              const errorMessage = 'Search request failed';
              showLdapError(errorMessage);
              throw new Error(errorMessage);
            }
            return response.json();
          })
          .then(jsonData => {
            if (jsonData.ldapLookupError) {
              showLdapError(jsonData.ldapLookupError);
            } else {
              document.getElementById('first_name').value = jsonData.firstName;
              document.getElementById('last_name').value = jsonData.lastName;
              document.getElementById('email').value = jsonData.email;
              document.getElementById('institution').value = jsonData.institution;
              document.getElementById('email').dispatchEvent(new Event('change'));
            }
          })
          .catch(reason => console.error(reason));
      });
    }

    // For LDAP users, require the email address to belong to the configured LDAP domain.
    if (ldapEmailDomain && emailInput && authenticationMethodInput) {
      const emailFeedback = findErrorDivForElement(emailInput);
      const defaultEmailFeedback = emailFeedback ? emailFeedback.textContent : '';
      const checkEmailDomain = function () {
        return validateLdapEmail(emailInput, authenticationMethodInput.value, ldapEmailDomain,
          emailFeedback, defaultEmailFeedback);
      };

      // Check email domain when input changes or 
      emailInput.addEventListener('input', () => {
        emailInput.classList.toggle('is-invalid', !checkEmailDomain());
      });
      emailInput.addEventListener('change', () => {
        emailInput.classList.toggle('is-invalid', !checkEmailDomain());
      });
      authenticationMethodInput.addEventListener('change', () => {
        const valid = checkEmailDomain();
        emailInput.classList.toggle('is-invalid', !valid && emailInput.value.trim() !== '');
      });

      // Set the initial validity without clearing any server-side error styling
      checkEmailDomain();
    }

    // Display any server validation errors included in hidden elements
    const serverValidationErrors = document.querySelectorAll('[data-error]');
    for (const serverError of serverValidationErrors) {
      const inputElement = document.getElementById(serverError.dataset.field);
      if (inputElement) {
        inputElement.classList.add('is-invalid');
        const feedbackElement = findErrorDivForElement(inputElement);
        if (feedbackElement) {
          feedbackElement.textContent = serverError.dataset.message;
        }
      }
    }
  });
})();
