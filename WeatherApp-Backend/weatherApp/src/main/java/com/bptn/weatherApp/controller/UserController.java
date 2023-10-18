package com.bptn.weatherApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bptn.weatherApp.jpa.User;
import com.bptn.weatherApp.service.UserService;

@CrossOrigin(exposedHeaders = "Authorization")
@RestController
@RequestMapping("/user")
public class UserController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired 
	UserService userService;
	
	@PostMapping("/signup")
	public User signup(@RequestBody User user) {	
		logger.debug("Signing up, username: {}", user.getUsername());
		return this.userService.signup(user);
	}
	
	@GetMapping("/verify/email")
	 public void verifyEmail() {
	   
	  logger.debug("Verifying Email");
	   
	  this.userService.verifyEmail();
	 }
}
