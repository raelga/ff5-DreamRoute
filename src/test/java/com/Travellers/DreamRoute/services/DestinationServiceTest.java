package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class DestinationServiceTest {
    @Mock
    DestinationRepository destinationRepository;

    @Mock
    DestinationMapperImpl destinationMapperImpl;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    DestinationService destinationService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("usertest");
    }

    @Test
    void shouldReturnListOfDestinationResponses() {
        Destination destination1 = new Destination(1L,  "Colombia", "Santa Marta", "La más hermosa y maravillosa ciudad del mundo", "https://examplephoto-santamarta.jpg", user);
        Destination destination2 = new Destination(2L,  "Japón", "Tokio", "La más hermosa y maravillosa ciudad del mundo después de Santa Marta", "https://examplephoto-tokio.jpg", user);

        given(destinationMapperImpl.entityToDto(destination1)).willReturn(
          new DestinationResponse(
                  destination1.getId(),
                  destination1.getCountry(),
                  destination1.getCity(),
                  destination1.getDescription(),
                  destination1.getImage(),
                  user.getUsername()
          )
        );
        given(destinationMapperImpl.entityToDto(destination2)).willReturn(
          new DestinationResponse(
                  destination2.getId(),
                  destination2.getCountry(),
                  destination2.getCity(),
                  destination2.getDescription(),
                  destination2.getImage(),
                  user.getUsername()
          )
        );

        given(destinationRepository.findAll()).willReturn(List.of(destination1, destination2));

        List<DestinationResponse> result = destinationService.getAllDestinations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).country()).isEqualTo("Colombia");
        assertThat(result.get(1).country()).isEqualTo("Japón");
        assertThat(result.get(0).city()).isEqualTo("Santa Marta");
        assertThat(result.get(1).city()).isEqualTo("Tokio");
        assertThat(result.get(0).description()).isEqualTo("La más hermosa y maravillosa ciudad del mundo");
        assertThat(result.get(1).description()).isEqualTo("La más hermosa y maravillosa ciudad del mundo después de Santa Marta");
        assertThat(result.get(0).image()).isEqualTo("https://examplephoto-santamarta.jpg");
        assertThat(result.get(1).image()).isEqualTo("https://examplephoto-tokio.jpg");
        assertThat(result.get(0).username()).isEqualTo("usertest");
        assertThat(result.get(1).username()).isEqualTo("usertest");
    }

    @Test
    void shouldReturnDestinationResponseGivenAnId() {
        Destination destination = new Destination(1L,  "Colombia", "Santa Marta", "La más hermosa y maravillosa ciudad del mundo", "https://examplephoto-santamarta.jpg", user);

        given(destinationMapperImpl.entityToDto(destination)).willReturn(
                new DestinationResponse(
                        destination.getId(),
                        destination.getCountry(),
                        destination.getCity(),
                        destination.getDescription(),
                        destination.getImage(),
                        user.getUsername()
                )
        );

        given(destinationRepository.findById(1L)).willReturn(Optional.of(destination));

        DestinationResponse result = destinationService.getDestinationById(1L);

        assertThat(result.country()).isEqualTo("Colombia");
        assertThat(result.city()).isEqualTo("Santa Marta");
        assertThat(result.description()).isEqualTo("La más hermosa y maravillosa ciudad del mundo");
        assertThat(result.image()).isEqualTo("https://examplephoto-santamarta.jpg");
        assertThat(result.username()).isEqualTo("usertest");
    }

}
