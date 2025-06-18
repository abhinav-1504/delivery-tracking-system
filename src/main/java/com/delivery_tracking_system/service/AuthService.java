package com.delivery_tracking_system.service;

import com.delivery_tracking_system.dto.JwtResponseDTO;
import com.delivery_tracking_system.dto.LoginDTO;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.exception.BadRequestException; // ✅ Correct import
import com.delivery_tracking_system.repository.UserRepository;
import com.delivery_tracking_system.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponseDTO login(LoginDTO loginDTO) throws BadRequestException {
        logger.info("Attempting login for user: {}", loginDTO.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        String token = jwtTokenProvider.generateToken(authentication);

        logger.info("Login successful for user: {}", loginDTO.getUsername());

        return new JwtResponseDTO(token, user.getRole());
    }

    public User register(User user) throws BadRequestException {
        logger.info("Registering new user: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
