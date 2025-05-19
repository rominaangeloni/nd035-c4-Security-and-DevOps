package com.example.demo.controllers;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Optional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById_UserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testFindById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUserName_UserExists() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    public void testFindByUserName_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("unknown");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUser_Successful() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        User user = new User();
        Cart cart = new Cart();

        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("newuser", response.getBody().getUsername());
        assertEquals("encodedPassword", response.getBody().getPassword());
        assertNotNull(response.getBody().getCart());

        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("shortpass");
        request.setPassword("123");
        request.setConfirmPassword("123");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(400, response.getStatusCodeValue());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("mismatch");
        request.setPassword("password1");
        request.setConfirmPassword("password2");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(400, response.getStatusCodeValue());
        verify(userRepository, never()).save(any(User.class));
    }
}
