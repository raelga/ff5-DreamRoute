package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.security.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DestinationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String asJsonString(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private ResultActions performGetRequest(String url) throws Exception{
        return mockMvc.perform(get(url)
                .with(user("testuser").roles("USER"))
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPostRequest(String url, Object body, UserDetail userDetail) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body))
                .with(user(userDetail))
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPostRequestUnauthenticated(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body))
                .accept(MediaType.APPLICATION_JSON));
    }

    @Nested
    @DisplayName("Get /destinations")
    class getAllDestinationTest {

        @Test
        @DisplayName("should return all destinations with status 200 OK and correct content type")
        void getAllDestinations_returnsListOfDestinations() throws Exception {
            performGetRequest("/destinations")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(10)))
                    .andExpect(jsonPath("$[0].country", is("Colombia")))
                    .andExpect(jsonPath("$[0].city", is("Santa Marta")))
                    .andExpect(jsonPath("$[9].country", is("Argentina")));
        }

        @Test
        @DisplayName("Should return destinations with expected structure and data types")
        void getAllDestinations_returnsCorrectStructureAndTypes() throws Exception {
            performGetRequest("/destinations")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].country").isString())
                    .andExpect(jsonPath("$[0].city").isString())
                    .andExpect(jsonPath("$[0].description").isString())
                    .andExpect(jsonPath("$[0].image").isString())
                    .andExpect(jsonPath("$[0].username").isString());
        }
    }

    @Nested
    @DisplayName("GET /destinations/{id}")
    class GetDestinationByIdTests {
        private final Long EXISTING_DESTINATION_ID = 1L;
        private final Long NON_EXISTING_DESTINATION_ID = 99L;

        @Test
        @DisplayName("Should return the destination by ID with status 200 OK")
        void getDestinationById_returnsDestination_whenIdExists() throws Exception {
            performGetRequest("/destinations/" + EXISTING_DESTINATION_ID)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(EXISTING_DESTINATION_ID.intValue())))
                    .andExpect(jsonPath("$.country", is("Colombia")))
                    .andExpect(jsonPath("$.city", is("Santa Marta")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when destination ID does not exist")
        void getDestinationById_returnsNotFound_whenIdDoesNotExist() throws Exception {
            performGetRequest("/destinations/" + NON_EXISTING_DESTINATION_ID)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /destinations/user/{userId}")
    class GetDestinationsByUserIdTests {
        private final Long USER_WITH_DESTINATION_ID = 1L;
        private final Long USER_WITHOUT_DESTINATION_ID = 6L;
        private final Long NON_EXISTENT_USER_ID = 99L;

        @Test
        @DisplayName("Should return a list of destinations for an existing user with destinations")
        void getDestinationsByUserId_returnsDestinations_whenUserExistsAndHasDestinations() throws Exception {
            performGetRequest("/destinations/user/" + USER_WITH_DESTINATION_ID)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$.[0].username", is("May")));
        }

        @Test
        @DisplayName("Should return an empty list when user exists but has no destinations")
        void getDestinationsByUserId_returnsEmptyList_whenUserExistsButNoDestinations() throws Exception {
            performGetRequest("/destinations/user/" + USER_WITHOUT_DESTINATION_ID)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", empty()));
        }

        @Test
        @DisplayName("Should return 404 Not Found when user ID does not exist")
        void getDestinationsByUserId_returnsNotFound_whenUserDoesNotExist() throws Exception {
            performGetRequest("/destinations/user/" + NON_EXISTENT_USER_ID)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /destinations")
    class AddDestinationTests{

        private static final String ROLE_USER_NAME = "ROLE_USER";
        private static final String ROLE_ADMIN_NAME = "ROLE_ADMIN";

        private User userEntityUserRole;
        private UserDetail userDetailUserRole;

        private User userEntityAdminRole;
        private UserDetail userDetailAdminRole;

        private User userEntityNonExistent;
        private UserDetail userDetailNonExistent;

        private DestinationRequest validDestinationRequest;

        private final String EXISTING_USERNAME_USER = "Deb";
        private final String EXISTING_USERNAME_ADMIN = "May";
        private final String NON_EXISTENT_USERNAME = "usertest";

        private Role createRole(String roleName) {
            Role role = new Role();
            role.setRoleName(roleName);
            return role;
        }

        @BeforeEach
        void setup(){
            validDestinationRequest = new DestinationRequest(
                    "Francia",
                    "Paris",
                    "La ciudad del amor.",
                    "https://example.com/paris.jpg"
            );

            Role userRole = createRole(ROLE_USER_NAME);

            userEntityUserRole = User.builder()
                    .id(2L)
                    .username(EXISTING_USERNAME_USER)
                    .password("any_encoded_password")
                    .roles(Collections.singletonList(userRole))
                    .build();
            userDetailUserRole = new UserDetail(userEntityUserRole);

            Role adminRole = createRole(ROLE_ADMIN_NAME);

            userEntityAdminRole = User.builder()
                    .id(1L)
                    .username(EXISTING_USERNAME_ADMIN)
                    .password("any_encoded_password")
                    .roles(Collections.singletonList(adminRole))
                    .build();
            userDetailAdminRole = new UserDetail(userEntityAdminRole);

            Role nonExistentUserRole = createRole(ROLE_USER_NAME);

            userEntityNonExistent = User.builder()
                    .id(999L)
                    .username(NON_EXISTENT_USERNAME)
                    .password("any_encoded_password")
                    .roles(Collections.singletonList(nonExistentUserRole))
                    .build();
            userDetailNonExistent = new UserDetail(userEntityNonExistent);
        }

        @Test
        @DisplayName("Should create a new destination when authenticated as USER with valid data (201 Created)")
        void addDestination_createsNewDestination_whenUserAuthenticatedAndValid() throws Exception {
            performPostRequest("/destinations", validDestinationRequest, userDetailUserRole)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.country", is("Francia")))
                    .andExpect(jsonPath("$.city", is("Paris")))
                    .andExpect(jsonPath("$.username", is(EXISTING_USERNAME_USER)));

            mockMvc.perform(get("/destinations")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(11)));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when authenticated as ADMIN (if only USER has permission)")
        void addDestination_returnsForbidden_whenAuthenticatedAsAdmin() throws Exception {
            performPostRequest("/destinations", validDestinationRequest, userDetailAdminRole)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is invalid (e.g., empty city)")
        void addDestination_returnsBadRequest_whenInvalidData() throws Exception{
            DestinationRequest invalidRequest = new DestinationRequest(
                    "Alemania",
                    "",
                    "Descripción válida.",
                    "https://example.com/berlin.jpg"
            );

            performPostRequest("/destinations", invalidRequest, userDetailUserRole)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details.city", is("City is required")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when authenticated user does not exist in DB")
        void addDestination_returnsNotFound_whenAuthenticatedUserDoesNotExistInDB() throws Exception{
            performPostRequest("/destinations", validDestinationRequest, userDetailNonExistent)
                    .andExpect(status().isNotFound());
        }
    }
}
