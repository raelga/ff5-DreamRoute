package com.Travellers.DreamRoute.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /register")
    class RegisterUser{

        @Test
        @DisplayName("should register a new user successfully")
        void shouldRegisterUserSuccessfully() throws Exception{
            String newUser = """
                    {
                    "username": "Laura",
                    "email": "laura@example.com",
                    "password": "Laura12345."
                    }
                    """;
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newUser))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").value("Laura"))
                    .andExpect(jsonPath("$.email").value("laura@example.com"));
        }

        @Test
        @DisplayName("should return 400 when username alredy exist")
        void shouldFailWhenUsernameAlreadyExists() throws Exception{
            String duplicatedUser = """
                    {
                    "username": "May",
                    "email": "nuevo@example.com",
                    "password": "Test12345."
                    }
                    """;
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicatedUser))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Username already taken"));
        }

        @Test
        @DisplayName("should return 400 when email already exists")
        void shouldFailWhenEmailAlreadyExists() throws Exception {
            String duplicatedEmail = """
                    {
                    "username": "Nuevo",
                    "email": "princesitarockera@gmail.com",
                    "password": "Test12345."
                    }
                    """;
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicatedEmail))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already registered"));
        }

        @Test
        @DisplayName("should return 400 when input is invalid")
        void shouldFailWhenInputIsInvalid() throws Exception {
            String invalidUser = """
                {
                    "username": "",
                    "email": "no-es-email",
                    "password": "123"
                }
            """;
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidUser))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details.username").exists())
                    .andExpect(jsonPath("$.details.email").exists())
                    .andExpect(jsonPath("$.details.password").exists());
        }
    }
}