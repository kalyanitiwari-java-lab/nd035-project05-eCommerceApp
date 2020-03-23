package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.ApplicationUser;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static  org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

    }


    //sanity test
    @Test
    public void create_user_happy_path(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();

        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<ApplicationUser> response =  userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        ApplicationUser user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    //sanity test
    @Test
    public void get_username_happy_path(){
        CreateUserRequest request = new CreateUserRequest();

        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<ApplicationUser> response1 =  userController.createUser(request);

        ApplicationUser testUser = response1.getBody();


        when(userRepo.findByUsername("testUser")).thenReturn(testUser);
        String username = "testUser";
        ResponseEntity<ApplicationUser> response2 = userController.findByUserName(username);
        assertNotNull(response2);
        ApplicationUser user = response2.getBody();
        assertNotNull(user);
        assertEquals(testUser, user);
    }
}
