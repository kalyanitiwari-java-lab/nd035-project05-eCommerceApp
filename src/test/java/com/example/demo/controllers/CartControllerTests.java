package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.ApplicationUser;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void init(){
        cartController = new CartController();

        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    //Sanity Test
    @Test
    public void add_to_cart_happy_path(){
        //Save a user
        ApplicationUser user = createUser();
        when(userRepo.findByUsername("cartUser")).thenReturn(user);

        //Modify cart
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(0);
        request.setQuantity(5);

       when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(getItem()));

        Cart modifiedCart = getUserCart(request.getItemId(), request.getQuantity());
        when(cartRepo.save(modifiedCart)).thenReturn(user.getCart());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Cart savedCart = response.getBody();
        assertNotNull(response);
        assertEquals(modifiedCart.getTotal(), savedCart.getTotal());
    }

    //negative test case
    @Test
    public void add_to_cart_error_flow(){
        //Add dirty cart
        Cart dirtyCart = getUserCart((long)0, 3);

        //Save a user
        ApplicationUser user = createUser();
        //User has a dirty cart
        user.setCart(dirtyCart);
        when(userRepo.findByUsername("cartUser")).thenReturn(user);

        //Modify cart
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(0);
        request.setQuantity(5);

        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(getItem()));

        Cart modifiedCart = getUserCart(request.getItemId(), request.getQuantity());
        when(cartRepo.save(modifiedCart)).thenReturn(user.getCart());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Cart savedCart = response.getBody();
        assertNotNull(response);
        assertNotEquals(modifiedCart.getTotal(), savedCart.getTotal());
    }

    private ApplicationUser createUser(){
        ApplicationUser user = new ApplicationUser();
        user.setUsername("cartUser");
        user.setPassword("Password");
        user.setCart(new Cart());
        return user;
    }

    private Item getItem(){
        Item item = new Item();
        item.setId((long)0);
        item.setName("Round Ridge");
        item.setDescription("A ridge that is round.");
        item.setPrice(new BigDecimal(10.00));
       return item;
    }

    private Cart getUserCart(long itemId, int quantity){
        Cart cart = new Cart();
        IntStream.range(0, quantity)
                .forEach(i ->  cart.addItem(getItem()));
        return cart;
    }

}
