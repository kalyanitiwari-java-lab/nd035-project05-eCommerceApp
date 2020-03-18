package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByApplicationUser(ApplicationUser user);
}
