package com.example.demo.controllers;

import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.*;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Cart cart;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("Test Item");

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setCart(cart);
        cart.setUser(user);
    }

    private ModifyCartRequest createRequest(int quantity) {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(quantity);
        return request;
    }

    @Test
    public void add_to_cart_successfully() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = createRequest(2);
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(BigDecimal.valueOf(20.0), response.getBody().getTotal());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void add_to_cart_user_not_found() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        ModifyCartRequest request = createRequest(1);
        request.setUsername("unknown");

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(404, response.getStatusCodeValue());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void add_to_cart_item_not_found() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = createRequest(1);
        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(404, response.getStatusCodeValue());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void remove_from_cart_successfully() {
        // Pre-fill cart with items
        IntStream.range(0, 3).forEach(i -> cart.addItem(item));

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = createRequest(2);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(BigDecimal.valueOf(10.0), response.getBody().getTotal());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void remove_from_cart_user_not_found() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        ModifyCartRequest request = createRequest(1);
        request.setUsername("ghost");

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(404, response.getStatusCodeValue());
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest request = createRequest(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(404, response.getStatusCodeValue());
        verify(cartRepository, never()).save(any());
    }
}
