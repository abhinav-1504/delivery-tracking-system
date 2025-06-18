package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.JwtResponseDTO;
import com.delivery_tracking_system.dto.LoginDTO;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.BadRequestException;
import com.delivery_tracking_system.repository.UserRepository;
import com.delivery_tracking_system.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginDTO loginDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole("CUSTOMER");

        loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("password");
    }

    @Test
    public void testLoginSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        JwtResponseDTO response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    public void testLoginFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadRequestException("Invalid username or password"));

        assertThrows(BadRequestException.class, () -> authService.login(loginDTO));
    }

    @Test
    public void testRegisterSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = authService.register(user);

        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUsernameExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.register(user));
    }
}