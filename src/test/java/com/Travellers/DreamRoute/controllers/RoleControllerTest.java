package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.JwtResponse;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for RoleController using JWT")
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String jwt;

    @BeforeEach
    void setUp() throws Exception{
    Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
            .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN", null)));

        String ADMIN_USERNAME = "testAdminUser";
        String ADMIN_EMAIL = "testadmin@example.com";
        String ADMIN_PASSWORD = "testAdminPassword123.";

        Optional<User> existingAdmin = userRepository.findByUsernameIgnoreCase(ADMIN_USERNAME);

        if (existingAdmin.isEmpty()) {
        User adminUserEntity = User.builder()
                .username(ADMIN_USERNAME)
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .roles(List.of(adminRole))
                .build();
        userRepository.save(adminUserEntity);
    }

    UserRequest adminLoginRequest = new UserRequest(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD);

    MvcResult loginResult = mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adminLoginRequest)))
            .andExpect(status().isOk())
            .andReturn();

    JwtResponse jwtResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(),
            JwtResponse.class
    );
        this.jwt = jwtResponse.token();
    }

    @Test
    @DisplayName("GET /roles should return all roles and a 200 status")
    void shouldReturnAllRoles() throws Exception{
        mockMvc.perform(get("/roles")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Nested
    @DisplayName("GET /roles/{id}")
    class getRoleById {

        @Test
        @DisplayName("should return the role with a status 200")
        void shouldReturnRoleById() throws Exception {
            Role role = roleRepository.findAll().getFirst();

            mockMvc.perform(get("/roles/" + role.getId())
                    .header("Authorization", "Bearer " + jwt))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(role.getId()))
                    .andExpect(jsonPath("$.roleName").value(role.getRoleName()));
        }

        @Test
        @DisplayName("should return a 404 not found when user ID does not exist ")
        void shouldReturn404WhenRoleNotFound() throws Exception {
            long nonExistentId = 99L;

            mockMvc.perform(get("/roles/" + nonExistentId)
                    .header("Authorization", "Bearer " + jwt))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Role not found with id " + nonExistentId));

        }

        @Test
        @DisplayName("Without authentication should return a 403")
        void shouldReturn403WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/roles"))
                    .andExpect(status().isForbidden());
        }
    }
}
