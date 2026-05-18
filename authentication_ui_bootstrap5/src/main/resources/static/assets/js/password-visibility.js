/**
 * Adds the password visibility toggle button to inputs with the `data-password-toggle` attribute.
 */
(function() {
  const inputSelector = 'input[data-password-toggle]';
  const iconSelector = '[data-password-toggle-icon]';
  const toggleStatusSelector = '[data-password-toggle-status]';

  function togglePassword(event) {
    const toggleButton = event.currentTarget;
    const passwordInput = toggleButton.parentElement.querySelector(inputSelector);
    const toggleIcon = toggleButton.querySelector(iconSelector);
    const toggleStatus = toggleButton.querySelector(toggleStatusSelector);

    if (!passwordInput || !toggleIcon || !toggleStatus) {
      console.error('Required elements not found');
      console.log(passwordInput, toggleIcon, toggleStatus);
      return;
    }

    toggleIcon.classList.toggle('fa-eye');
    toggleIcon.classList.toggle('fa-eye-slash');

    const inputType = passwordInput.getAttribute('type');
    if (inputType === 'password') {
      passwordInput.setAttribute('type', 'text');
      toggleStatus.textContent = 'Your password is visible';
    } else {
      passwordInput.setAttribute('type', 'password');
      toggleStatus.textContent = 'Your password is hidden';
    }
  }

  function wrapInput(inputElement) {
    if (inputElement.getAttribute('type') !== 'password') {
      console.error('Not adding password visibility toggle to non-password input.', inputElement);
    }

    // add padding to prevent obscuring the password
    inputElement.classList.add('pe-5');

    const wrapper = document.createElement('div');
    wrapper.classList.add('position-relative');

    const toggleButton = document.createElement('button');
    toggleButton.setAttribute('type', 'button');
    toggleButton.setAttribute('title', 'toggle password visibility');
    toggleButton.classList.add('show-password-toggle');
    toggleButton.innerHTML = '<span class="fa-regular fa-eye-slash" data-password-toggle-icon></span><span class="visually-hidden" aria-live="polite" data-password-toggle-status></span>';

    inputElement.parentElement.appendChild(wrapper);
    wrapper.appendChild(inputElement);
    wrapper.appendChild(toggleButton);
    toggleButton.addEventListener('click', togglePassword);
  }

  const inputs = document.querySelectorAll(inputSelector);
  if (inputs.length) {
    inputs.forEach(wrapInput);
    inputs[0].form.addEventListener('submit', function(event) {
      const elements = event.target.querySelectorAll(inputSelector);
      elements.forEach(function(element) {
        element.setAttribute('type', 'password');
      });
    });
  }
})();