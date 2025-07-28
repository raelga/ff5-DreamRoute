package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.security.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("UserController Integration Tests")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse expectedUserMayResponse;

    @BeforeEach
    void setUp() {
        expectedUserMayResponse = new UserResponse(
                1L,
                "May",
                "princesitarockera@gmail.com",
                List.of("Santa Marta", "Sídney", "Bariloche"),
                List.of("ROLE_ADMIN")
        );
    }

    private String asJsonString(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private ResultActions performGetRequest(String urlTemplate, Object... uriVars) throws Exception {
        return mockMvc.perform(get(urlTemplate, uriVars)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Nested
    @DisplayName("Get /users/all")
    class GetAllUsersTest {

        @Test
        @DisplayName("should return all users with status 200 OK and correct content type")
        void getAllUsers_returnsListOfUsers() throws Exception {
            performGetRequest("/users/all", "May", "ROLE_ADMIN")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(6)))
                    .andExpect(jsonPath("$[0].username", is("May")))
                    .andExpect(jsonPath("$[0].email", is("princesitarockera@gmail.com")))
                    .andExpect(jsonPath("$[5].username", is("Violeta")));
        }

        @Test
        @DisplayName("Should return users with expected structure and data types")
        void getAllUsers_returnsCorrectStructureAndTypes() throws Exception {
            performGetRequest("/users/all", "May", "ADMIN")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].username").isString())
                    .andExpect(jsonPath("$[0].email").isString())
                    .andExpect(jsonPath("$[0].roles").isArray());
        }

        @Test
        @DisplayName("should return 403 Forbidden if not authenticated (or unauthorized)")
        void getAllUsers_returnsUnauthorizedWhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/users/all")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should return 403 Forbidden when a normal USER tries to get all users")
        void getAllUsers_asNormalUser_shouldReturnForbidden() throws Exception {
            performGetRequest("/users/all", "Deb", "USER")
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("GET /users/{username}")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("should return 200 OK and UserResponse for existing username")
        @WithMockUser(username = "May", roles = {"ADMIN"})
        void shouldGetUserByUsernameSuccessfully() throws Exception {
            String username = "May";

            performGetRequest("/users/{username}", username)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(expectedUserMayResponse.id()))
                    .andExpect(jsonPath("$.username").value(expectedUserMayResponse.username()))
                    .andExpect(jsonPath("$.email").value(expectedUserMayResponse.email()))
                    .andExpect(jsonPath("$.destinations").isArray())
                    .andExpect(jsonPath("$.destinations[0]").value("Santa Marta"))
                    .andExpect(jsonPath("$.destinations[1]").value("Sídney"))
                    .andExpect(jsonPath("$.destinations[2]").value("Bariloche"))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
        }

        @Test
        @DisplayName("should return 404 Not Found when user does not exist")
        @WithMockUser(username = "May", roles = {"ADMIN"})
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            String usernameDoesNotExist = "nonexistentuser";

            performGetRequest("/users/{username}", usernameDoesNotExist)
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("User not found with username " + usernameDoesNotExist));
        }


    }
}