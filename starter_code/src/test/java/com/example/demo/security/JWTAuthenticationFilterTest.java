package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private JWTAuthenticationFilter filter;

    @Before
    public void setup() {
        filter = new JWTAuthenticationFilter(authenticationManager);
    }

    @Test
    public void attemptAuthentication_shouldAuthenticateUser() throws Exception {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("pass");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                new ObjectMapper().writeValueAsBytes(user));
        Mockito.when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(inputStream));

        Authentication authResult = new UsernamePasswordAuthenticationToken("user1", "pass", new ArrayList<>());
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authResult);

        Authentication result = filter.attemptAuthentication(request, response);
        assertNotNull(result);
        assertEquals("user1", result.getPrincipal());
    }

    @Test
    public void successfulAuthentication_shouldAddTokenToResponse() throws Exception {
        org.springframework.security.core.userdetails.User user =
                new org.springframework.security.core.userdetails.User("user1", "pass", new ArrayList<>());

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

        filter.successfulAuthentication(request, response, chain, auth);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(response).addHeader(eq(SecurityConstants.HEADER_STRING), headerCaptor.capture());

        String tokenHeader = headerCaptor.getValue();
        assertTrue(tokenHeader.startsWith(SecurityConstants.TOKEN_PREFIX));
    }
}

