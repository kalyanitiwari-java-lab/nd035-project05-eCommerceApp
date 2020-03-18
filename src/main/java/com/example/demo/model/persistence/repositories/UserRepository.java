package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
	ApplicationUser findByUsername(String username);
}
