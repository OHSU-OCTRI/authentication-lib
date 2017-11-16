package org.octri.authentication.server.rest;

import java.util.HashMap;
import java.util.Map;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController {

	@Autowired
	private UserService userService;

	@RequestMapping(path = "admin/user/taken/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Map<String, Object> taken(@PathVariable("username") String username, Model model) {
		User existing = userService.findByUsername(username);
		Map<String, Object> out = new HashMap<>();
		out.put("taken", existing == null ? false : true);
		return out;
	}

}
