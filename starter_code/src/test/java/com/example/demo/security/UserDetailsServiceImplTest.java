package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        String username = "testuser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        String username = "nonexistentuser";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(null);

        userDetailsService.loadUserByUsername(username);
    }
}

