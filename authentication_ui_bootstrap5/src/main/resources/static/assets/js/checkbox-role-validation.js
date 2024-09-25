/**
 * Attaches a listener that uses the javascript Constraint Validation API to validate that
 * at least one role has been selected.
 *
 * See:
 *  - https://getbootstrap.com/docs/5.3/forms/validation/
 *  - https://stackoverflow.com/questions/48785768/how-to-group-checkboxes-for-validation-in-bootstrap
 */
(function() {
  'use strict';

  function anyChecked(elements) {
    return elements.some(el => el.checked);
  }

  function noneChecked(elements) {
    return !anyChecked(elements);
  }

  function setRequired(elements) {
    elements.forEach(el => el.required = true);
  }

  function clearRequired(elements) {
    elements.forEach(el => el.required = false);
  }

  function emphasizeElement(element) {
    element.classList.add('text-danger');
    element.classList.remove('text-secondary');
  }

  function deemphasizeElement(element) {
    element.classList.add('text-secondary');
    element.classList.remove('text-danger');
  }

  window.addEventListener('load', function() {
    const userRoleGroup = document.getElementById('user_role_group');
    const userRoleDescription = document.getElementById('user_role_description');
    const userRoleCheckboxes = document.querySelectorAll('input[name=userRoles]');

    if (!userRoleGroup || !userRoleDescription || !userRoleCheckboxes.length) {
      return;
    }

    // initialize the required property if nothing is checked
    const checkboxArray = Array.from(userRoleCheckboxes);
    if (noneChecked(checkboxArray)) {
      setRequired(checkboxArray);
    }

    // add/remove required property as boxes are checked
    userRoleGroup.addEventListener('change', function(_evt) {
      if (anyChecked(checkboxArray)) {
        clearRequired(checkboxArray);
        deemphasizeElement(userRoleDescription);
      } else {
        setRequired(checkboxArray);
        emphasizeElement(userRoleDescription);
      }
    });

    // highlight #user_role_description on submit if no roles are selected
    checkboxArray[0].form.addEventListener('submit', function(_evt) {
      if (noneChecked(checkboxArray)) {
        emphasizeElement(userRoleDescription);
      } else {
        deemphasizeElement(userRoleDescription);
      }
    })
  }, false);
})();