package com.delivery_tracking_system.controller;

import com.delivery_tracking_system.dto.JwtResponseDTO;
import com.delivery_tracking_system.dto.LoginDTO;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid username or password")
    })
    public JwtResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) throws BadRequestException {
        logger.info("Login request for user: {}", loginDTO.getUsername());
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    @Operation(summary = "USer registration", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Username already exists")
    })
    public User register(@RequestBody User user) throws BadRequestException {
        logger.info("Registering user: {}", user.getUsername());
        return authService.register(user);
    }
}
