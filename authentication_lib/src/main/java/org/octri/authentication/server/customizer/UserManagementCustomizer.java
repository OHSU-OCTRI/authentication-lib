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

        /**
         * Default redirect after user creating or updating users.
         */
        public static final String DEFAULT_REDIRECT = "redirect:/admin/user/list";

        /**
         * Hook that is called before saving the given user. If the optional {@link ModelAndView} is present, the
         * save is aborted, and the ModelAndView is rendered instead. The default implementation does nothing.
         *
         * @param user
         *                the user entity to be saved
         * @param model
         *                data to use when rendering the view
         * @param request
         *                user form request data
         * @return an optional ModelAndView. If present, the save is aborted, and the ModelAndView is rendered.
         */
        default Optional<ModelAndView> beforeSaveAction(User user, ModelMap model, HttpServletRequest request) {
                return Optional.empty();
        }

        /**
         * Hook that is called after a new user is created. The returned {@link ModelAndView} is rendered. The default
         * implementation redirects to the user list page.
         *
         * @param user
         *                the user that was just created
         * @param model
         *                data to use when rendering the view
         * @param request
         *                user form request data
         * @return ModelAndView to render
         */
        default ModelAndView postCreateAction(User user, ModelMap model, HttpServletRequest request) {
                return new ModelAndView(DEFAULT_REDIRECT);
        }

        /**
         * Hook that is called after a user is updated. The returned {@link ModelAndView} is rendered. The default
         * implementation redirects to the user list page.
         *
         * @param user
         *                the user that was just updated
         * @param model
         *                data to use when rendering the view
         * @param request
         *                user form request data
         * @return ModelAndView to render
         */
        default ModelAndView postUpdateAction(User user, ModelMap model, HttpServletRequest request) {
                return new ModelAndView(DEFAULT_REDIRECT);
        }

}
