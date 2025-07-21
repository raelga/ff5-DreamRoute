package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;

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

    @Test
    void shouldReturnListOfDestinationResponseGivenAUserId() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("May");
        user1.setEmail("princesitarockera@gmail.com");
        user1.setPassword("May12345.");
        User user4 = new User();
        user4.setId(4L);
        user4.setUsername("Nia");
        user4.setPassword("Nia12345.");

        Destination destination1a = new Destination(1L,  "Colombia", "Santa Marta", "La más hermosa y maravillosa ciudad del mundo", "https://examplephoto-santamarta.jpg", user1);
        Destination destination2 = new Destination(7L,  "Australia", "Sídney", "Icono de la costa australiana con playas, ópera y naturaleza.", "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583236/sydney-img_hgjycy.jpg", user1);
        Destination destination3 = new Destination(10L,  "Argentina", "Bariloche", "Paisajes de montaña, lagos y chocolate en la Patagonia argentina.", "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583239/bariloche-img_jsqzbg.jpg", user1);
        Destination destination4 = new Destination(9L,  "Francia", "París", "La ciudad del amor con su icónica Torre Eiffel, museos y gastronomía.", "https://res.cloudinary.com/dwc2jpfbw/image/upload/v1752583238/paris-img_jgcsje.jpg", user4);

        given(userRepository.findById(1L)).willReturn(Optional.of(user1));


        given(destinationMapperImpl.entityToDto(destination1a)).willReturn(
                new DestinationResponse(
                        destination1a.getId(),
                        destination1a.getCountry(),
                        destination1a.getCity(),
                        destination1a.getDescription(),
                        destination1a.getImage(),
                        user1.getUsername()
                )
        );
        given(destinationMapperImpl.entityToDto(destination2)).willReturn(
                new DestinationResponse(
                        destination2.getId(),
                        destination2.getCountry(),
                        destination2.getCity(),
                        destination2.getDescription(),
                        destination2.getImage(),
                        user1.getUsername()
                )
        );
        given(destinationMapperImpl.entityToDto(destination3)).willReturn(
                new DestinationResponse(
                        destination3.getId(),
                        destination3.getCountry(),
                        destination3.getCity(),
                        destination3.getDescription(),
                        destination3.getImage(),
                        user1.getUsername()
                )
        );


        given(destinationRepository.findAllByUser(user1)).willReturn(List.of(destination1a, destination2, destination3));

        List<DestinationResponse> result1 = destinationService.getDestinationsByUserId(1L);

        assertThat(result1).hasSize(3);
        assertThat(result1.get(0).country()).isEqualTo("Colombia");
        assertThat(result1.get(0).city()).isEqualTo("Santa Marta");
        assertThat(result1.get(0).description()).isEqualTo("La más hermosa y maravillosa ciudad del mundo");
        assertThat(result1.get(0).image()).isEqualTo("https://examplephoto-santamarta.jpg");
        assertThat(result1.get(0).username()).isEqualTo("May"); //era usertest
    }

    @Test
    void shouldAddDestinationSuccessfully() {
        DestinationRequest request = new DestinationRequest("Chile", "Santiago", "una ciudad increible", "http://image.png");

        Destination savedDestination = new Destination(
                1L,
                "Chile",
                "Santiago",
                "una ciudad increible",
                "http://image.png",
                user
        );


        given(userRepository.findByUsernameIgnoreCase("usertest")).willReturn(Optional.of(user));
        given(destinationMapperImpl.dtoToEntity(request, user)).willReturn(savedDestination);
        given(destinationRepository.save(savedDestination)).willReturn(savedDestination);
        given(destinationMapperImpl.entityToDto(savedDestination)).willReturn(new DestinationResponse(1L,
                "Chile",
                "Santiago",
                "una ciudad increible",
                "http://image.png",
                "usertest"));

        DestinationResponse response = destinationService.addDestination(request, "usertest");

        assertNotNull(response.id());
        assertEquals("Chile", response.country());
        assertEquals("Santiago", response.city());
        assertEquals("una ciudad increible", response.description());
        assertEquals("http://image.png", response.image());
        assertEquals("usertest", response.username());
    }

}
