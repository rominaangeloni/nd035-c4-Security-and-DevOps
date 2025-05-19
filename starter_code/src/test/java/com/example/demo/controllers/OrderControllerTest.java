package com.example.demo.controllers;

import com.example.demo.model.persistence.*;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Cart cart = new Cart();
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(9.99));
        cart.setItems(Arrays.asList(item));
        cart.setUser(user);
        cart.setTotal(BigDecimal.valueOf(9.99));

        user.setCart(cart);
        return user;
    }

    @Test
    public void submit_order_successfully() {
        User user = createTestUser();
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(9.99), order.getTotal());

        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void submit_order_user_not_found() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("unknown");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void get_order_history_successfully() {
        User user = createTestUser();
        UserOrder order = UserOrder.createFromCart(user.getCart());

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void get_order_history_user_not_found() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("ghost");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}

