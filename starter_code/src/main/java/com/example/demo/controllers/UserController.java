package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("Fetching user by ID: {}", id);
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("Fetching user by username: {}", username);
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.warn("User not found: {}", username);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("Received request to create user: {}", createUserRequest.getUsername());

		try {
			if (createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
				logger.warn("CreateUser failed for {}: Password validation failed", createUserRequest.getUsername());
				return ResponseEntity.badRequest().build();
			}

			if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
				logger.warn("CreateUser failed for {}: Username already exists", createUserRequest.getUsername());
				return ResponseEntity.badRequest().build();
			}

			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);

			userRepository.save(user);

			logger.info("CreateUser request successful for user: {}", user.getUsername());
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			logger.error("Exception while creating user {}: {}", createUserRequest.getUsername(), e.getMessage(), e);
			return ResponseEntity.internalServerError().build();
		}
	}
}
