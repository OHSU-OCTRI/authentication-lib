package org.octri.authentication.server.customizer;

import java.util.Optional;

import org.octri.authentication.server.security.entity.User;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The interface defining hooks into the UserController that an application can override
 */
public interface UserManagementCustomizer {

    public Optional<ModelAndView> beforeSaveAction(User user, ModelMap model,
            HttpServletRequest request);

    public ModelAndView postCreateAction(User user, ModelMap model,
            HttpServletRequest request);

    public ModelAndView postUpdateAction(User user, ModelMap model,
            HttpServletRequest request);

}
