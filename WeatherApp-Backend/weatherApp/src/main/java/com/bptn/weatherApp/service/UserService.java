package com.bptn.weatherApp.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bptn.weatherApp.exception.domain.EmailExistException;
import com.bptn.weatherApp.exception.domain.UserNotFoundException;
import com.bptn.weatherApp.exception.domain.UsernameExistException;
import com.bptn.weatherApp.jpa.User;
import com.bptn.weatherApp.repository.UserRepository;


@Service
public class UserService {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	EmailService emailService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	private void validateUsernameAndEmail(String username, String emailId) {

		this.userRepository.findByUsername(username).ifPresent(u -> {
			throw new UsernameExistException(String.format("Username already exists, %s", u.getUsername()));
		});

		this.userRepository.findByEmailId(emailId).ifPresent(u -> {
			throw new EmailExistException(String.format("Email already exists, %s", u.getEmailId()));
		});

}
	public User signup(User user) {

		user.setUsername(user.getUsername().toLowerCase());
		user.setEmailId(user.getEmailId().toLowerCase());

		this.validateUsernameAndEmail(user.getUsername(), user.getEmailId());

		user.setEmailVerified(false);
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
	
		user.setCreatedOn(Timestamp.from(Instant.now()));

	    this.userRepository.save(user);

		this.emailService.sendVerificationEmail(user);
		
		return user;
	}
	
	public void verifyEmail() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		user.setEmailVerified(true);

		this.userRepository.save(user);
}
	
	

}
