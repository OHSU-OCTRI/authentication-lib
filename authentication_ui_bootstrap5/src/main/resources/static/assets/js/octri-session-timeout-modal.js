/* eslint-disable no-console */

/**
 * Notifies the user when their session is about to expire and automatically redirects to
 * a logout URL if they do not respond.
 *
 * TODO: Consider refactoring to remove the dependency on Bootstrap's modal JS.
 *
 * The constructor accepts an options object with the following properties.
 *
 * Required properties:
 *
 * - contextPath (string): Root server path of the application (e.g. '/' or '/myapp').
 * - logoutTimeoutSeconds (string|number): Seconds to wait between displaying the warning dialog and logging the user out.
 * - warnTimeoutSeconds (string|number): Seconds to wait before displaying the warning dialog.
 *
 * Optional properties. See `defaults` in the constructor for default values.
 *
 * - keepaliveButtonId (string): ID of the button element that refreshes the user's session.
 * - keepalivePath (string): Server path of the keepalive endpoint. Relative to contextPath.
 * - logoutCallback (() => Promise<any>): Callback to invoke before logging the user out, e.g. to save state.
 * - logoutMessage (string): Message to display when logging the user out.
 * - logoutPath (string): Server path log the user out. Relative to contextPath.
 * - messageElementId (string): ID of the HTML element that should display messages to the user.
 * - modalId (string): ID of the root element of the modal dialog.
 * - warningMessage (string): Message to display to the user when their session is about to expire.
 */
class OctriSessionTimeoutModal {
  options = null;
  warnTimer = null;
  logoutTimer = null;

  constructor(options) {
    const defaults = {
      contextPath: null,
      keepaliveButtonId: 'session_timeout_modal_keepalive_button',
      keepalivePath: '/keepalive',
      logoutCallback: () => Promise.resolve(),
      logoutMessage: 'Logging out.',
      logoutPath: '/logout',
      logoutTimeoutSeconds: null,
      messageElementId: 'session_timeout_modal_message',
      modalId: 'session_timeout_modal',
      warningMessage:
        'You will automatically log out in 2 minutes. Use the button below to stay logged in.',
      warnTimeoutSeconds: null
    };

    this.options = Object.assign({}, defaults, options);

    // strip trailing slash from the context path to simplify URL construction
    const { contextPath } = this.options;
    if (contextPath && contextPath.endsWith('/')) {
      this.options.contextPath = contextPath.slice(0, contextPath.length - 1);
    }
  }

  getWarnTimeoutMs() {
    return this.options.warnTimeoutSeconds * 1000;
  }

  getLogoutTimeoutMs() {
    return this.options.logoutTimeoutSeconds * 1000;
  }

  getLogoutPath() {
    return `${this.options.contextPath}${this.options.logoutPath}`;
  }

  getModal() {
    const modalId = this.options.modalId;
    return bootstrap.Modal.getInstance(document.getElementById(modalId));
  }

  getKeepaliveButton() {
    return document.getElementById(this.options.keepaliveButtonId);
  }

  showModal() {
    const modal = this.getModal();
    this.setMessage(this.options.warningMessage);
    modal.show();
    clearTimeout(this.warnTimer);
    this.logoutTimer = setTimeout(() => this.onLogout(), this.getLogoutTimeoutMs());
  }

  setMessage(msg) {
    const messageElement = document.getElementById(this.options.messageElementId);
    messageElement.textContent = msg;
  }

  onRefreshSession() {
    const modal = this.getModal();
    const contextPath = this.options.contextPath;
    const keepalivePath = this.options.keepalivePath;

    fetch(`${contextPath}${keepalivePath}?ts=${Date.now()}`).then(response => {
      console.info(response);
      if (response.ok) {
        modal.hide();
        this.warnTimer = setTimeout(() => this.showModal(), this.getWarnTimeoutMs());
        clearTimeout(this.logoutTimer);
      } else {
        this.onLogout();
      }
    });
  }

  onLogout() {
    console.log('Logging out');
    clearTimeout(this.logoutTimer);

    this.setMessage(this.options.logoutMessage);
    const keepaliveButton = this.getKeepaliveButton();
    if (keepaliveButton) {
      keepaliveButton.remove();
    }

    this.options.logoutCallback().finally(() => {
      window.location = this.getLogoutPath();
    });
  }

  init() {
    const requiredOptions = ['contextPath', 'logoutTimeoutSeconds', 'warnTimeoutSeconds'];
    for (const optionName of requiredOptions) {
      if (this.options[optionName] === null || this.options[optionName] === undefined) {
        console.error(
          `Missing required option ${optionName}. Session timeout JS not installed.`
        );
        return;
      }
    }

    const modalElement = document.getElementById(this.options.modalId);
    const keepaliveButton = document.getElementById(this.options.keepaliveButtonId);
    const messageElement = document.getElementById(this.options.messageElementId);
    if (!modalElement || !keepaliveButton || !messageElement) {
      console.error(
        'Could not find all the required elements. Session timeout JS not installed.'
      );
      return;
    }

    const modal = new bootstrap.Modal(modalElement);
    modalElement.addEventListener('hidden.bs.modal', () => this.onRefreshSession());
    this.warnTimer = setTimeout(() => this.showModal(), this.getWarnTimeoutMs());
  }
}
