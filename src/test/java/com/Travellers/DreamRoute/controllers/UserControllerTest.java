package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.user.JwtResponse;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.dtos.user.UserUpdateRequest;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import com.Travellers.DreamRoute.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String adminJwt;
    private String userJwt;
    private Long adminId;
    private Long userId;
    private Long anotherUserId;

    private final String ADMIN_USERNAME = "adminTestUser";
    private final String ADMIN_EMAIL = "admin@test.com";
    private final String USER_USERNAME = "userTestUser";

    @BeforeEach
    void setUp() throws Exception {

        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.findByRoleNameIgnoreCase("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER", null)));
        Role adminRole = roleRepository.findByRoleNameIgnoreCase("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN", null)));

        String ADMIN_PASSWORD = "AdminPassword123!";
        User adminUser = User.builder()
                .username(ADMIN_USERNAME)
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .roles(List.of(adminRole))
                .build();
        userRepository.save(adminUser);
        adminId = adminUser.getId();

        String USER_EMAIL = "user@test.com";
        String USER_PASSWORD = "UserPassword123!";
        User normalUser = User.builder()
                .username(USER_USERNAME)
                .email(USER_EMAIL)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .roles(List.of(userRole))
                .build();
        userRepository.save(normalUser);
        userId = normalUser.getId();

        adminJwt = performLogin(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD);
        userJwt = performLogin(USER_USERNAME, USER_EMAIL, USER_PASSWORD);

        String ANOTHER_USERNAME = "anotherTestUser";
        String ANOTHER_EMAIL = "another@test.com";
        String ANOTHER_PASSWORD = "AnotherPassword123!";
        User anotherUser = User.builder()
                .username(ANOTHER_USERNAME)
                .email(ANOTHER_EMAIL)
                .password(passwordEncoder.encode(ANOTHER_PASSWORD))
                .roles(List.of(userRole))
                .build();
        userRepository.save(anotherUser);
        anotherUserId = anotherUser.getId();

        User user = new User();
        user.setUsername("May");

        Role role = Role.builder()
                .roleName("ROLE_ADMIN")
                .build();

        user.setRoles(List.of(role));

    }

    private String performLogin(String username, String email, String password) throws Exception {
        UserRequest loginRequest = new UserRequest(username, email, password);
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), JwtResponse.class);
        return jwtResponse.token();
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
                .header("Authorization", "Bearer " + adminJwt)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPutRequest(Long id, UserUpdateRequest requestBody, String jwtToken) throws Exception {
        return mockMvc.perform(put("/users/update/{id}", id)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)));
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
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].username", is(ADMIN_USERNAME)))
                    .andExpect(jsonPath("$[0].email", is(ADMIN_EMAIL)))
                    .andExpect(jsonPath("$[1].username", is(USER_USERNAME)));
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
        @DisplayName("should return 403 Forbidden when a normal USER tries to get all users")
        void getAllUsers_asNormalUser_shouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/users/all")
                            .header("Authorization", "Bearer " + userJwt)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    @DisplayName("GET /users/{username}")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("should return 200 OK and UserResponse for existing username")
        void shouldGetUserByUsernameSuccessfully() throws Exception {

            performGetRequest("/users/{username}", ADMIN_USERNAME)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(adminId))
                    .andExpect(jsonPath("$.username").value(ADMIN_USERNAME))
                    .andExpect(jsonPath("$.email").value(ADMIN_EMAIL))
                    .andExpect(jsonPath("$.destinations").isArray())
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
        }

        @Test
        @DisplayName("should return 404 Not Found when user does not exist")
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            String usernameDoesNotExist = "nonexistentuser";

            performGetRequest("/users/{username}", usernameDoesNotExist)
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("User not found with username " + usernameDoesNotExist));
        }

        @Test
        @DisplayName("should return 401 Unauthorized if not authenticated")
        void getByUsername_returnsUnauthorizedWhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/users/{username}", ADMIN_USERNAME)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /users/update/{id}")
    class UpdateUserTest {

        @Test
        @DisplayName("Admin should be able to update their own username and email")
        void adminCanUpdateOwnProfile() throws Exception {
            String newUsername = "newAdminUsername";
            String newEmail = "newadmin@test.com";
            UserUpdateRequest updateRequest = new UserUpdateRequest(newUsername, newEmail, null, null);

            performPutRequest(adminId, updateRequest, adminJwt)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(newUsername))
                    .andExpect(jsonPath("$.email").value(newEmail))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

            User updatedUser = userRepository.findById(adminId).orElseThrow();
            assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
            assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("Admin should be able to update another user's username and email")
        void adminCanUpdateAnotherUserProfile() throws Exception {
            String newUsername = "updatedAnotherUser";
            String newEmail = "updated_another@test.com";
            UserUpdateRequest updateRequest = new UserUpdateRequest(newUsername, newEmail, null, null);

            performPutRequest(anotherUserId, updateRequest, adminJwt)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(newUsername))
                    .andExpect(jsonPath("$.email").value(newEmail));

            User updatedUser = userRepository.findById(anotherUserId).orElseThrow();
            assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
            assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        }

        @Test
        @DisplayName("Admin should be able to change another user's roles")
        void adminCanChangeAnotherUserRoles() throws Exception {
            User anotherUserInDb = userRepository.findById(anotherUserId)
                    .orElseThrow(() -> new RuntimeException("Test setup error: anotherUser not found"));

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    anotherUserInDb.getUsername(),
                    anotherUserInDb.getEmail(),
                    null,
                    List.of("ROLE_USER")
            );

            performPutRequest(anotherUserId, updateRequest, adminJwt)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles.length()").value(1))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

            User updatedUser = userRepository.findById(anotherUserId).orElseThrow();
            assertThat(updatedUser.getRoles()).hasSize(1);
            assertThat(updatedUser.getRoles().getFirst().getRoleName()).isEqualTo("ROLE_USER");
            assertThat(updatedUser.getUsername()).isEqualTo(anotherUserInDb.getUsername());
            assertThat(updatedUser.getEmail()).isEqualTo(anotherUserInDb.getEmail());
        }

        @Test
        @DisplayName("Admin should NOT be able to change another user's password")
        void adminCannotChangeAnotherUserPassword() throws Exception {
            String newPassword = "newPassword123.";
            User anotherUserInDb = userRepository.findById(anotherUserId)
                    .orElseThrow(() -> new RuntimeException("Test setup error: anotherUser not found"));

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    anotherUserInDb.getUsername(),
                    anotherUserInDb.getEmail(),
                    newPassword,
                    List.of()
            );

            performPutRequest(anotherUserId, updateRequest, adminJwt)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Admins are not allowed to change passwords of other users"));
        }

        @Test
        @DisplayName("Admin should be able to change their own password")
        void adminCanChangeOwnPassword() throws Exception {
            String newPassword = "newAdminPassword123.";
            User adminUserInDb = userRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Test setup error: adminUser not found"));

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    adminUserInDb.getUsername(),
                    adminUserInDb.getEmail(),
                    newPassword,
                    List.of()
            );

            performPutRequest(adminId, updateRequest, adminJwt)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(adminId));

            User updatedUser = userRepository.findById(adminId).orElseThrow();
            AssertionsForClassTypes.assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
        }

        @Test
        @DisplayName("Normal user should be able to update their own username, email and password")
        void userCanUpdateOwnProfile() throws Exception {
            String newUsername = "newUserTest";
            String newEmail = "newuser@test.com";
            String newPassword = "newPasswordUser123.";
            UserUpdateRequest updateRequest = new UserUpdateRequest(newUsername, newEmail, newPassword, List.of());

            performPutRequest(userId, updateRequest, userJwt)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(newUsername))
                    .andExpect(jsonPath("$.email").value(newEmail));

            User updatedUser = userRepository.findById(userId).orElseThrow();
            assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
            assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
            AssertionsForClassTypes.assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
        }

        @Test
        @DisplayName("Normal user should NOT be able to change their own roles")
        void userCannotChangeOwnRoles() throws Exception {
            User currentUserInDb = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Test setup error: currentUser not found"));

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    currentUserInDb.getUsername(),
                    currentUserInDb.getEmail(),
                    null,
                    List.of("ROLE_ADMIN")
            );

            performPutRequest(userId, updateRequest, userJwt)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Users are not allowed to change their own roles"));
        }

        @Test
        @DisplayName("Normal user should NOT be able to update another user's profile")
        void userCannotUpdateAnotherUserProfile() throws Exception {
            String newUsername = "shouldNotChange";
            User anotherUserInDb = userRepository.findById(anotherUserId)
                    .orElseThrow(() -> new RuntimeException("Test setup error: anotherUser not found"));

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    anotherUserInDb.getUsername(),
                    anotherUserInDb.getEmail(),
                    null,
                    List.of()
            );

            performPutRequest(anotherUserId, updateRequest, userJwt)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You don't have permission to update this user"));
        }

        @Test
        @DisplayName("Should return 404 Not Found when updating non-existent user")
        void shouldReturn404WhenUserNotFound() throws Exception {
            Long nonExistentId = 99L;
            UserUpdateRequest updateRequest = new UserUpdateRequest("nonExistent", "nonexistent@test.com", null, null);

            performPutRequest(nonExistentId, updateRequest, adminJwt)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id " + nonExistentId));
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when no JWT is provided")
        void shouldReturn401WhenNoJwt() throws Exception {
            UserUpdateRequest updateRequest = new UserUpdateRequest("test", "test@test.com", null, null);

            mockMvc.perform(put("/users/update/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails (e.g., blank username)")
        void shouldReturn400OnValidationFail() throws Exception {
            UserUpdateRequest updateRequest = new UserUpdateRequest("", "valid@email.com", null, null);

            performPutRequest(userId, updateRequest, userJwt)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /users/delete/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("should return OK and success message when admin deletes an existing user")
        void shouldDeleteUserSuccessfullyWhenAdmin() throws Exception {
            Long userIdToDelete = anotherUserId;
            String expectedMessage = "User with id " + userIdToDelete + " has been deleted";

            User adminUser = userRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalStateException("Admin user not found in DB for test"));
            UserDetail testAdminUserDetail = new UserDetail(adminUser);

            mockMvc.perform(delete("/users/delete/{id}", userIdToDelete)
                            .with(user(testAdminUserDetail))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedMessage));
        }

        @Test
        @DisplayName("should return 403 forbidden when normal user tries to delete a user")
        void shouldReturnForbiddenWhenNormalUserDeletesUser() throws Exception {
            Long userIdToDelete = 3L;

            User normalUser = new User();
            normalUser.setId(2L);
            normalUser.setUsername("Deb");
            normalUser.setPassword("dummyPassword");

            Role userRole = new Role(1L, "ROLE_USER", null);
            normalUser.setRoles(List.of(userRole));

            UserDetail testNormalUser = new UserDetail(normalUser);

            mockMvc.perform(delete("/users/delete/{id}", userIdToDelete)
                            .with(user(testNormalUser))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Only administrators can delete users"));
        }

        @Test
        @DisplayName("should return 404 not found when admin tries to delete a non-existent user")
        void shouldReturnNotFoundWhenAdminDeletesNonExistentUser() throws Exception {
            Long nonExistentUserId = 999L;

            User adminUser = new User();
            adminUser.setId(1L);
            adminUser.setUsername("May");
            adminUser.setPassword("dummyPassword");

            Role adminRole = new Role(2L, "ROLE_ADMIN", null);
            adminUser.setRoles(List.of(adminRole));

            UserDetail testAdmin = new UserDetail(adminUser);

            mockMvc.perform(delete("/users/delete/{id}", nonExistentUserId)
                            .with(user(testAdmin))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id " + nonExistentUserId));
        }
    }

}