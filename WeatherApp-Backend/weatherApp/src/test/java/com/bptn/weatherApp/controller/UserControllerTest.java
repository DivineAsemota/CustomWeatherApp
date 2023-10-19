package com.bptn.weatherApp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bptn.weatherApp.jpa.User;
import com.bptn.weatherApp.repository.UserRepository;
import com.bptn.weatherApp.security.JwtService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest {

	User user;
	String otherUsername;
	String otherPassword;

	@MockBean
	Server server;

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setup() {
		user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setUsername("john.doe");
		user.setPassword("password");
		user.setPhone("1234567890");
		user.setEmailId("john.doe@example.com");
		otherUsername = "anotherUser";
		otherPassword = "anotherPassword";
	}

	@Test
	@Order(1)
	public void signupIntegrationTest() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();
		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(this.user.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(this.user.getLastName())))
				.andExpect(jsonPath("$.username", is(this.user.getUsername())))
				.andExpect(jsonPath("$.phone", is(this.user.getPhone())))
				.andExpect(jsonPath("$.emailId", is(this.user.getEmailId())));
		/* Check the DB */
		Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());
		assertTrue(opt.isPresent(), "User Should Exist");
		assertEquals(1, opt.get().getUserId());
		assertEquals(this.user.getFirstName(), opt.get().getFirstName());
		assertEquals(this.user.getLastName(), opt.get().getLastName());
		assertEquals(this.user.getUsername(), opt.get().getUsername());
		assertEquals(this.user.getPhone(), opt.get().getPhone());
		assertEquals(this.user.getEmailId(), opt.get().getEmailId());
		assertEquals(false, opt.get().getEmailVerified());
		assertTrue(this.passwordEncoder.matches(this.user.getPassword(), opt.get().getPassword()));
	}





	@Test
	@Order(2)
	public void signupUsernameExistsIntegrationTest() throws Exception {

		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(this.user)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST"))).andExpect(jsonPath("$.message",
						is(String.format("Username already exists, %s", this.user.getUsername()))));
	}

	@Test
	@Order(3)
	public void signupEmailExistsIntegrationTest() throws Exception {

		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build();

		this.user.setUsername(this.otherUsername);

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(this.user)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST"))).andExpect(
						jsonPath("$.message", is(String.format("Email already exists, %s", this.user.getEmailId()))));
	}

}
