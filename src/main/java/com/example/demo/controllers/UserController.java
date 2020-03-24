package com.example.demo.controllers;

import com.example.demo.model.persistence.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
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

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<ApplicationUser> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<ApplicationUser> findByUserName(@PathVariable String username) {
		ApplicationUser user = userRepository.findByUsername(username);
		if(user == null){
			log.debug("User not found " + username);
			return ResponseEntity.notFound().build();
		}else {
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApplicationUser> createUser(@RequestBody CreateUserRequest createUserRequest) {
		System.out.println("Creating user {}: " + createUserRequest.getUsername());
		ApplicationUser user = new ApplicationUser();
		user.setUsername(createUserRequest.getUsername());
		log.info("User name set with ", createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length() < 7 || !(createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword()))){
			//System.out.println("Error with user password. Cannot create user {}: " + createUserRequest.getUsername());
			log.debug("Error with user password. Cannot create user {}: ", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		log.info("Create user successful.");
		return ResponseEntity.ok(user);
	}
	
}
