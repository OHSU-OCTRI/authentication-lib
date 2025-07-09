package org.octri.authentication.server.controller;

import java.util.Optional;

import org.octri.authentication.server.security.AuthenticationUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller that handles fetch requests to keep a user's session alive.
 */
@Controller
public class KeepaliveController {

    private static final Logger log = LoggerFactory.getLogger(KeepaliveController.class);

    /**
     * Handles session keepalive requests.
     * 
     * @param userDetails
     *            the current user; null if not authenticated
     * @param timestamp
     *            optional timestamp parameter
     * @return an "OK" response
     */
    @GetMapping("/keepalive")
    public ResponseEntity<String> keepAlive(@AuthenticationPrincipal AuthenticationUserDetails userDetails,
            @RequestParam("ts") Optional<String> timestamp) {
        log.debug("Keepalive request: user={} timestamp={}", userDetails, timestamp);
        return ResponseEntity.ok("OK");
    }

}
