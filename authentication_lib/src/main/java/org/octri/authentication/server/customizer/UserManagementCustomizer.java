package org.octri.authentication.server.customizer;

import org.octri.authentication.server.security.entity.User;

/**
 * The interface defining hooks into the UserController that an application can override
 */
public interface UserManagementCustomizer {

    public String postCreateAction(User user);

    public String postUpdateAction(User user);

}
