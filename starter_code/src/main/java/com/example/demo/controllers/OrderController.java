package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				logger.warn("Order submission failed: user '{}' not found", username);
				return ResponseEntity.notFound().build();
			}
			UserOrder order = UserOrder.createFromCart(user.getCart());
			orderRepository.save(order);
			logger.info("Order submitted successfully for user '{}'", username);
			return ResponseEntity.ok(order);
		} catch (Exception e) {
			logger.error("Exception occurred while submitting order for user '{}'", username, e);
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				logger.warn("Order history request failed: user '{}' not found", username);
				return ResponseEntity.notFound().build();
			}
			List<UserOrder> orders = orderRepository.findByUser(user);
			logger.info("Order history retrieved successfully for user '{}'", username);
			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			logger.error("Exception occurred while retrieving order history for user '{}'", username, e);
			return ResponseEntity.internalServerError().build();
		}
	}
}
