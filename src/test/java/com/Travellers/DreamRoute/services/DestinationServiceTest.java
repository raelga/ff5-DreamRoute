package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.destination.DestinationMapperImpl;
import com.Travellers.DreamRoute.dtos.destination.DestinationRequest;
import com.Travellers.DreamRoute.dtos.destination.DestinationResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Destination;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.DestinationRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@DisplayName("DestinationService Unit Tests")
public class DestinationServiceTest {
    @Mock
    DestinationRepository destinationRepository;

    @Mock
    DestinationMapperImpl destinationMapperImpl;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    DestinationService destinationService;

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return role;
    }

    @Nested
    @DisplayName("getAllDestinations()")
    class GetAllDestinationsTests {

        private User testUser;
        private Destination testDestination1;
        private Destination testDestination2;
        private DestinationResponse testDestinationResponse1;
        private DestinationResponse testDestinationResponse2;

        @BeforeEach
        void setup() {
            testUser = User.builder()
                    .id(1L)
                    .username("usertest")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();

            testDestination1 = new Destination(1L, "Colombia", "Santa Marta", "Desc1", "url1", testUser);
            testDestination2 = new Destination(2L, "Japón", "Tokio", "Desc2", "url2", testUser);

            testDestinationResponse1 = new DestinationResponse(
                    testDestination1.getId(), testDestination1.getCountry(), testDestination1.getCity(),
                    testDestination1.getDescription(), testDestination1.getImage(), testUser.getUsername()
            );
            testDestinationResponse2 = new DestinationResponse(
                    testDestination2.getId(), testDestination2.getCountry(), testDestination2.getCity(),
                    testDestination2.getDescription(), testDestination2.getImage(), testUser.getUsername()
            );

            given(destinationMapperImpl.entityToDto(testDestination1)).willReturn(testDestinationResponse1);
            given(destinationMapperImpl.entityToDto(testDestination2)).willReturn(testDestinationResponse2);
        }

        @Test
        @DisplayName("Should return a list of all destination responses")
        void shouldReturnListOfDestinationResponses() {
            given(destinationRepository.findAll()).willReturn(List.of(testDestination1, testDestination2));
            List<DestinationResponse> result = destinationService.getAllDestinations();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(testDestinationResponse1, testDestinationResponse2);
            verify(destinationRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getDestinationById(Long id)")
    class GetDestinationByIdTests {

        private User testUser;
        private Destination testDestination;
        private DestinationResponse testDestinationResponse;

        @BeforeEach
        void setup() {
            testUser = User.builder()
                    .id(1L)
                    .username("usertest")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();

            testDestination = new Destination(1L, "Colombia", "Santa Marta", "Desc", "url", testUser);
            testDestinationResponse = new DestinationResponse(
                    testDestination.getId(), testDestination.getCountry(), testDestination.getCity(),
                    testDestination.getDescription(), testDestination.getImage(), testUser.getUsername()
            );
        }

        @Test
        @DisplayName("Should return DestinationResponse given a valid ID")
        void shouldReturnDestinationResponseGivenAnId() {
            Long destinationId = 1L;

            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(testDestination));
            given(destinationMapperImpl.entityToDto(testDestination)).willReturn(testDestinationResponse);

            DestinationResponse result = destinationService.getDestinationById(destinationId);

            assertThat(result).isEqualTo(testDestinationResponse);
            verify(destinationRepository).findById(destinationId);
            verify(destinationMapperImpl).entityToDto(testDestination);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when Destination ID is not found")
        void shouldThrowEntityNotFoundException_whenIdNotFound() {
            Long nonExistentId = 99L;
            given(destinationRepository.findById(nonExistentId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> destinationService.getDestinationById(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Destination not found with id " + nonExistentId);

            verify(destinationRepository).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("addDestination(DestinationRequest, UserDetail)")
    class AddDestinationTests {
        private User testUser;
        private UserDetail testUserDetail;
        private DestinationRequest validRequest;
        private Destination savedDestinationEntity;
        private DestinationResponse expectedResponse;

        @BeforeEach
        void setup() {
            testUser = User.builder()
                    .id(1L)
                    .username("usertest")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();
            testUserDetail = new UserDetail(testUser);

            validRequest = new DestinationRequest("Chile", "Santiago", "una ciudad increible", "http://image.png");
            savedDestinationEntity = Destination.builder()
                    .id(1L)
                    .country(validRequest.country())
                    .city(validRequest.city())
                    .description(validRequest.description())
                    .image(validRequest.image())
                    .user(testUser)
                    .build();
            expectedResponse = new DestinationResponse(
                    savedDestinationEntity.getId(), savedDestinationEntity.getCountry(), savedDestinationEntity.getCity(),
                    savedDestinationEntity.getDescription(), savedDestinationEntity.getImage(), testUser.getUsername()
            );
        }

        @Test
        @DisplayName("Should add a destination successfully when user is valid")
        void shouldAddDestinationSuccessfully_whenUserIsValid() {
            given(userRepository.findByUsernameIgnoreCase(testUser.getUsername())).willReturn(Optional.of(testUser));
            given(destinationMapperImpl.dtoToEntity(validRequest, testUser)).willReturn(savedDestinationEntity);
            given(destinationRepository.save(savedDestinationEntity)).willReturn(savedDestinationEntity);
            given(destinationMapperImpl.entityToDto(savedDestinationEntity)).willReturn(expectedResponse);

            DestinationResponse response = destinationService.addDestination(validRequest, testUserDetail);

            assertThat(response).isEqualTo(expectedResponse);
            verify(userRepository).findByUsernameIgnoreCase(testUser.getUsername());
            verify(destinationRepository).save(savedDestinationEntity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UserDetail is missing or invalid")
        void shouldThrowIllegalArgumentException_whenUserDetailsIsInvalid() {
            assertThatThrownBy(() -> destinationService.addDestination(validRequest, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User information is missing or invalid");

            verify(userRepository, never()).findByUsernameIgnoreCase(org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when user does not exist in DB")
        void shouldThrowNoSuchElementException_whenUserDoesNotExistInDB() {
            given(userRepository.findByUsernameIgnoreCase(testUser.getUsername())).willReturn(Optional.empty());
            assertThatThrownBy(() -> destinationService.addDestination(validRequest, testUserDetail))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("User not found");

            verify(userRepository).findByUsernameIgnoreCase(testUser.getUsername());
            verify(destinationRepository, never()).save(org.mockito.ArgumentMatchers.any());
        }
    }

}