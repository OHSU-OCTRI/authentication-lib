package org.octri.authentication.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mustache.MustacheProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * A configuration class that verifies that the templates used to render the UI are present and warns if any are
 * missing.
 */
@Configuration
@ConditionalOnProperty(value = "octri.authentication.ui.check-templates", havingValue = "true", matchIfMissing = true)
public class TemplateConfiguration {

	private static final Log log = LogFactory.getLog(TemplateConfiguration.class);

	private final String[] TEMPLATE_PATHS = new String[] {
			"admin/user/form.mustache",
			"admin/user/list.mustache",
			"authlib_fragments/admin/user/form.mustache",
			"authlib_fragments/admin/user/list.mustache",
			"authlib_fragments/assets.mustache",
			"authlib_fragments/css.mustache",
			"authlib_fragments/forms/csrf_input.mustache",
			"authlib_fragments/forms/save_cancel_buttons.mustache",
			"authlib_fragments/forms/user/user_save_error.mustache",
			"authlib_fragments/login_form.mustache",
			"authlib_fragments/login.mustache",
			"authlib_fragments/navbar.mustache",
			"authlib_fragments/user/password/forgot.mustache",
			"authlib_fragments/user/password/form.mustache",
			"authlib_fragments/user/password/password_info.mustache",
			"components/messages.mustache",
			"error.mustache",
			"login.mustache",
			"user/password/forgot.mustache",
			"user/password/form.mustache"
	};

	private MustacheProperties mustacheProperties;
	private ApplicationContext applicationContext;

	public TemplateConfiguration(MustacheProperties mustacheProperties, ApplicationContext applicationContext) {
		this.mustacheProperties = mustacheProperties;
		this.applicationContext = applicationContext;
		checkTemplates();
	}

	private void checkTemplates() {
		String templatePrefix = mustacheProperties.getPrefix();

		log.info("Verifying that AuthLib UI templates exist.");
		List<String> missingTemplates = new ArrayList<>();
		for (String templateName : TEMPLATE_PATHS) {
			String templatePath = templatePrefix + templateName;
			Resource template = applicationContext.getResource(templatePath);

			if (template.exists()) {
				log.debug(templateName + " - present");
			} else {
				log.debug(templateName + " - missing");
				missingTemplates.add(templateName);
			}
		}

		if (!missingTemplates.isEmpty()) {
			log.warn("Template checking is enabled, but not all AuthLib templates were found. Install one of " +
					"the authentication UI packages, provide your own templates, or set " +
					"octri.authentication.ui.check-templates to false.");
			log.warn("Template location: " + templatePrefix);
			log.warn("Total templates: " + TEMPLATE_PATHS.length + " Missing: " + missingTemplates.size());
			log.warn("Missing templates: " + String.join(", ", missingTemplates));
		}
	}
}
