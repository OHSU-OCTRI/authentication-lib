package org.octri.authentication.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.octri.authentication.server.security.service.UserRoleService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	private MockMvc mvc;

	@Mock
	private UserService userService;

	@Mock
	private UserRoleService userRoleService;

	@Mock
	private Boolean ldapEnabled = true;

	@Mock
	private Boolean tableBasedEnabled = true;

	@InjectMocks
	private UserController userController;

	@Before
	public void beforeEach() {
		mvc = standaloneSetup(userController).build();
	}

	@Test
	public void testGetUserList() throws Exception {
		ResultActions actions = mvc.perform(get("/admin/user/list")).andExpect(status().isOk());
		MvcResult result = actions.andReturn();
	}

}
