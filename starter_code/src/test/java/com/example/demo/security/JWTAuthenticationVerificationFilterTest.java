package com.example.demo.security;

import com.auth0.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationVerificationFilterTest {

    @InjectMocks
    private JWTAuthenticationVerficationFilter filter;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Before
    public void setUp() {
        filter = new JWTAuthenticationVerficationFilter(authManager);
    }

    @Test
    public void doFilterInternal_shouldContinueFilterChain_whenHeaderIsNull() throws Exception {
        Mockito.when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_shouldContinueFilterChain_whenHeaderDoesNotStartWithBearer() throws Exception {
        Mockito.when(request.getHeader(SecurityConstants.HEADER_STRING)).thenReturn("InvalidToken");

        filter.doFilterInternal(request, response, chain);

        Mockito.verify(chain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_shouldAuthenticateUser_whenValidTokenProvided() throws Exception {
        String username = "testuser";
        String token = JWT.create()
                .withSubject(username)
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));

        Mockito.when(request.getHeader(SecurityConstants.HEADER_STRING))
                .thenReturn(SecurityConstants.TOKEN_PREFIX + token);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(username, auth.getPrincipal());
        Mockito.verify(chain).doFilter(request, response);
    }
}

