package com.delivery_tracking_system.controller;

import com.delivery_tracking_system.dto.JwtResponseDTO;
import com.delivery_tracking_system.dto.LoginDTO;
import com.delivery_tracking_system.dto.OrderDTO;
import com.delivery_tracking_system.entity.User;
import com.delivery_tracking_system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    public void setUp() throws Exception {
        if (userRepository.findByUsername("admin1").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setPassword(passwordEncoder.encode("12345"));
            admin.setEmail("admin1@example.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
        adminToken = getAdminToken();
    }

    @Test
    public void testCreateOrder() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setStatus("PENDING");
        orderDTO.setCustomerId(2L);
        orderDTO.setDeliveryAgentId(3L);
        orderDTO.setDeliveryAddress("123 Test St");

        mockMvc.perform(post("/api/admin/order")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        mockMvc.perform(put("/api/admin/order/1/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DELIVERED\""))
                .andExpect(status().isOk());
    }

    private String getAdminToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin1");
        loginDTO.setPassword("12345"); // Use the correct password

        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtResponseDTO response = objectMapper.readValue(result, JwtResponseDTO.class);
        return response.getToken();
    }
}