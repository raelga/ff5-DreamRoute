package com.Travellers.DreamRoute.controllers;

import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.services.DestinationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DestinationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DestinationService destinationService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<DestinationResponse> destinationResponses;

    @BeforeEach
    void setUp(){
        UserResponse user = new UserResponse(
                1L,
                "May",
                "princesitarockera@gmail.com",
                "May12345.",
                List.of("Santa Marta, Tokio"),
                List.of("ROLE_USER, ROLE_ADMIN"));

        destinationResponses = new ArrayList<>();

        DestinationResponse destination1 = new DestinationResponse(
                1L,
                "Colombia",
                "Santa Marta",
                "La más hermosa y maravillosa ciudad del mundo",
                "https://examplephoto-santamarta.jpg",
                user.username());
        DestinationResponse destination2 = new DestinationResponse(
                2L,
                "Japón",
                "Tokio",
                "La más hermosa y maravillosa ciudad del mundo después de Santa Marta",
                "https://examplephoto-tokio.jpg",
                user.username());

        destinationResponses.add(destination1);
        destinationResponses.add(destination2);
    }

    @Test
    void shouldGetAllDestinationsSuccessfully() throws Exception {
        given(destinationService.getAllDestinations()).willReturn(destinationResponses);

        mockMvc.perform(get("/destinations").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].country").value("Colombia"))
                .andExpect(jsonPath("$[1].country").value("Japón"))
                .andExpect(jsonPath("$[0].city").value("Santa Marta"))
                .andExpect(jsonPath("$[1].city").value("Tokio"))
                .andExpect(jsonPath("$[0].description").value("La más hermosa y maravillosa ciudad del mundo"))
                .andExpect(jsonPath("$[1].description").value("La más hermosa y maravillosa ciudad del mundo después de Santa Marta"))
                .andExpect(jsonPath("$[0].image").value("https://examplephoto-santamarta.jpg"))
                .andExpect(jsonPath("$[1].image").value("https://examplephoto-tokio.jpg"))
                .andExpect(jsonPath("$[0].username").value("May"))
                .andExpect(jsonPath("$[1].username").value("May"));
    }

    @Test
    void shouldGetDestinationByIdSuccessfully() throws Exception {
        given(destinationService.getDestinationById(1L)).willReturn(destinationResponses.getFirst());

        mockMvc.perform(get("/destinations/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.country").value("Colombia"))
                .andExpect(jsonPath("$.city").value("Santa Marta"))
                .andExpect(jsonPath("$.description").value("La más hermosa y maravillosa ciudad del mundo"))
                .andExpect(jsonPath("$.image").value("https://examplephoto-santamarta.jpg"))
                .andExpect(jsonPath("$.username").value("May"));
    }
}
