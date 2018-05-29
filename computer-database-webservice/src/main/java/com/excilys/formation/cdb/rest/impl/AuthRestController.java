package com.excilys.formation.cdb.rest.impl;

import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.formation.cdb.model.UserInfo;
import com.excilys.formation.cdb.model.constants.Paths;
import com.excilys.formation.cdb.security.config.SimpleAuthenticationService;

@RestController
@CrossOrigin(origins = "*")
public class AuthRestController {


	private SimpleAuthenticationService authentication;

	@Autowired
	public AuthRestController(SimpleAuthenticationService authService) {
		this.authentication = authService;
	}

	@CrossOrigin(origins = "*")
	@PostMapping(Paths.REST_LOGIN)
	public String login(HttpServletResponse response, @RequestBody UserInfo user) {
		Optional<String> authResultOpt = authentication.login(user.getUsername(), user.getPassword());
		if (!authResultOpt.isPresent()) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
		return authResultOpt.get();
	}


	@CrossOrigin(origins = "*")
	@PostMapping(Paths.REST_LOGOUT)
	public void logout(HttpServlet request, @RequestBody String token) {
		authentication.logout(token);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(Paths.REST_ROLE+"/{token}")
	public String getRole(@PathVariable String token) {
		return authentication.getRole(token);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(Paths.REST_REGISTER+"/{username}/{password}")
	public String addUser(@PathVariable String username, @PathVariable String password) {
		if (authentication.isPresent(username)) {
			return "This username is already taken";
		}
		authentication.createUser(username, password);
		return "New user successfully created";
			
	}

}
