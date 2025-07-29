package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.security.UserDetail;
import com.Travellers.DreamRoute.security.jwt.JwtService;
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
import java.util.Set;

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

    @Autowired
    private JwtService jwtService;

    private UserResponse expectedUserMayResponse;

    private String token;

    @BeforeEach
    void setUp() {
        expectedUserMayResponse = new UserResponse(
                1L,
                "May",
                "princesitarockera@gmail.com",
                List.of("Santa Marta", "Sídney", "Bariloche"),
                List.of("ROLE_ADMIN")
        );
        User user = new User();
        user.setUsername("May");

        Role role = Role.builder()
                .roleName("ROLE_ADMIN")
                .build();

        user.setRoles(List.of(role));

        UserDetail userDetail = new UserDetail(user);
        token = jwtService.generateToken(userDetail);
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
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Nested
    @DisplayName("Get /users/all")
    class GetAllUsersTest {

        @Test
        @DisplayName("should return all users with status 200 OK and correct content type")
        void getAllUsers_returnsListOfUsers() throws Exception {
            performGetRequest("/users/all")
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
            performGetRequest("/users/all")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].username").isString())
                    .andExpect(jsonPath("$[0].email").isString())
                    .andExpect(jsonPath("$[0].roles").isArray());
        }

        @Test
        @DisplayName("should return 401 Unauthorized if not authenticated")
        void getAllUsers_returnsUnauthorizedWhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/users/all")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "Deb", roles = {"USER"})
        @DisplayName("should return 403 Forbidden when a normal USER tries to get all users")
        void getAllUsers_asNormalUser_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/users/all")
                    .accept(MediaType.APPLICATION_JSON))
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

        @Nested
        @DisplayName("DELETE /users/delete/{id}")
        class DeleteUserTests {

            @Test
            @DisplayName("should return OK and success message when admin deletes an existing user")
            @WithMockUser(username = "May", roles = {"ADMIN"})
            void shouldDeleteUserSuccessfullyWhenAdmin() throws Exception {
                Long userIdToDelete = 3L;
                String expectedMessage = "User with id " + userIdToDelete + " has been deleted";

                User adminUser = new User();
                adminUser.setId(1L); // May's ID
                adminUser.setUsername("May");
                adminUser.setPassword("dummyPassword");

                Role adminRole = new Role(2L, "ROLE_ADMIN", null); // Ensure ID and name match your test-data.sql
                adminUser.setRoles(List.of(adminRole));

                UserDetail testAdmin = new UserDetail(adminUser);

                mockMvc.perform(delete("/users/delete/{id}", userIdToDelete)
                                .with(user(testAdmin))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string(expectedMessage));
            }

            @Test
            @DisplayName("should return 403 forbidden when normal user tries to delete a user")
            void shouldReturnForbiddenWhenNormalUserDeletesUser() throws Exception {
                Long userIdToDelete = 3L; //

                User normalUser = new User();
                normalUser.setId(2L); // Deb's ID
                normalUser.setUsername("Deb");
                normalUser.setPassword("dummyPassword");

                Role userRole = new Role(1L, "ROLE_USER", null); // ID 1 is ROLE_USER in test-data.sql
                normalUser.setRoles(List.of(userRole));

                UserDetail testNormalUser = new UserDetail(normalUser); // Represents Deb, the normal user

                mockMvc.perform(delete("/users/delete/{id}", userIdToDelete)
                                .with(user(testNormalUser)) // Simulate normal user
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden()) // Expect 403 Forbidden
                        .andExpect(jsonPath("$.message").value("Only administrators can delete users")); // Verify message
            }

            @Test
            @DisplayName("should return 404 not found when admin tries to delete a non-existent user")
            void shouldReturnNotFoundWhenAdminDeletesNonExistentUser() throws Exception {
                Long nonExistentUserId = 999L;

                User adminUser = new User();
                adminUser.setId(1L); // May's ID
                adminUser.setUsername("May");
                adminUser.setPassword("dummyPassword");

                Role adminRole = new Role(2L, "ROLE_ADMIN", null);
                adminUser.setRoles(List.of(adminRole));

                UserDetail testAdmin = new UserDetail(adminUser);

                mockMvc.perform(delete("/users/delete/{id}", nonExistentUserId)
                                .with(user(testAdmin)) // Simulate admin user
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound()) // Expect 404 Not Found
                        .andExpect(jsonPath("$.message").value("User not found with id " + nonExistentUserId));
            }
        }



}