/**
 * Installs AuthLib session timeout modal with default behavior.
 */
(function () {
  'use strict';
  window.addEventListener(
    'load',
    function () {
      if (!OctriSessionTimeoutModal) {
        console.error(
          'OctriSessionTimeoutModal class is not present. Timeout modal will not be installed.'
        );
        return;
      }

      const contextMeta = document.querySelector('meta[name=ctx]');
      const timeoutMeta = document.querySelector('meta[name=session-timeout-seconds]');
      if (contextMeta && timeoutMeta) {
        const contextPath = contextMeta.getAttribute('content');
        const sessionTimeoutSeconds = Number.parseInt(
          timeoutMeta.getAttribute('content')
        );

        if (!sessionTimeoutSeconds || sessionTimeoutSeconds < 120) {
          console.error(
            'Session timeout could not be parsed or is too short. Timeout modal will not be installed.'
          );
          return;
        }

        // warn 2 minutes before the session expires
        const warnTimeoutSeconds = sessionTimeoutSeconds - 120;

        // log off 10 seconds before the session expires
        const logoutTimeoutSeconds = sessionTimeoutSeconds - warnTimeoutSeconds - 10;

        new OctriSessionTimeoutModal({
          contextPath,
          warnTimeoutSeconds,
          logoutTimeoutSeconds
        }).init();
      } else {
        console.error(
          'The ctx or session-timeout-seconds meta tag is missing. Timeout modal will not be installed.'
        );
      }
    },
    false
  );
})();
