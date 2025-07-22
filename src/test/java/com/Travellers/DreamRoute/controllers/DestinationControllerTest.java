package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.services.DestinationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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
}
