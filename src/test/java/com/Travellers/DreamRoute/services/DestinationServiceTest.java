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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
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

            validRequest = new DestinationRequest("Chile", "Santiago", "una ciudad increíble", "http://image.png");
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

    @Nested
    @DisplayName("updateDestination(Long id, DestinationRequest destinationRequest, UserDetail userDetails)")
    class UpdateDestinationTests {
        private User ownerUser;
        private User otherUser;
        private User adminUser;
        private UserDetail ownerUserDetail;
        private UserDetail otherUserDetail;
        private UserDetail adminUserDetail;
        private Destination ownedDestination;
        private Destination otherDestination;
        private DestinationRequest updateRequest;
        private Destination updatedOwnedDestinationEntity;
        private DestinationResponse expectedResponse;

        @BeforeEach
        void setup() {
            ownerUser = User.builder()
                    .id(1L)
                    .username("ownerUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();
            ownerUserDetail = new UserDetail(ownerUser);

            otherUser = User.builder()
                    .id(2L)
                    .username("otherUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();
            otherUserDetail = new UserDetail(otherUser);

            adminUser = User.builder()
                    .id(3L)
                    .username("adminUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_ADMIN")))
                    .build();
            adminUserDetail = new UserDetail(adminUser);

            ownedDestination = new Destination(100L, "Honduras", "Roatán", "Isla bonita", "http://roatan.png", ownerUser);
            otherDestination = new Destination(200L, "Guatemala", "Antigua Guatemala", "Ciudad colonial", "http://antigua.png", otherUser);

            updateRequest = new DestinationRequest("Updated Country", "Updated City", "Updated Description", "http://updated.png");

            updatedOwnedDestinationEntity = Destination.builder()
                    .id(ownedDestination.getId())
                    .country(updateRequest.country())
                    .city(updateRequest.city())
                    .description(updateRequest.description())
                    .image(updateRequest.image())
                    .user(ownerUser)
                    .build();

            expectedResponse = new DestinationResponse(
                    updatedOwnedDestinationEntity.getId(),
                    updatedOwnedDestinationEntity.getCountry(),
                    updatedOwnedDestinationEntity.getCity(),
                    updatedOwnedDestinationEntity.getDescription(),
                    updatedOwnedDestinationEntity.getImage(),
                    ownerUser.getUsername()
            );
        }

        @Test
        @DisplayName("Should update destination successfully when authorized as owner")
        void shouldUpdateDestinationSuccessfully_whenAuthorizedAsOwner() {
            Long destinationId = ownedDestination.getId();

            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(ownedDestination));

            given(destinationRepository.save(ArgumentMatchers.any(Destination.class))).willReturn(updatedOwnedDestinationEntity);

            given(destinationMapperImpl.entityToDto(updatedOwnedDestinationEntity)).willReturn(expectedResponse);

            DestinationResponse result = destinationService.updateDestination(destinationId, updateRequest, ownerUserDetail);

            assertThat(result).isEqualTo(expectedResponse);
            verify(destinationRepository).findById(destinationId);

            verify(destinationRepository).save(ArgumentMatchers.any(Destination.class));
            verify(destinationMapperImpl).entityToDto(updatedOwnedDestinationEntity);
        }

        @Test
        @DisplayName("Should update destination successfully when authorized as admin")
        void shouldUpdateDestinationSuccessfully_whenAuthorizedAsAdmin() {
            Long destinationId = otherDestination.getId();
            Destination updatedOtherDestinationEntity = Destination.builder()
                    .id(otherDestination.getId())
                    .country(updateRequest.country())
                    .city(updateRequest.city())
                    .description(updateRequest.description())
                    .image(updateRequest.image())
                    .user(otherUser)
                    .build();
            DestinationResponse expectedAdminResponse = new DestinationResponse(
                    updatedOtherDestinationEntity.getId(),
                    updatedOtherDestinationEntity.getCountry(),
                    updatedOtherDestinationEntity.getCity(),
                    updatedOtherDestinationEntity.getDescription(),
                    updatedOtherDestinationEntity.getImage(),
                    otherUser.getUsername()
            );

            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(otherDestination));
            given(destinationRepository.save(ArgumentMatchers.any(Destination.class))).willReturn(updatedOtherDestinationEntity);
            given(destinationMapperImpl.entityToDto(updatedOtherDestinationEntity)).willReturn(expectedAdminResponse);

            DestinationResponse result = destinationService.updateDestination(destinationId, updateRequest, adminUserDetail);

            assertThat(result).isEqualTo(expectedAdminResponse);
            verify(destinationRepository).findById(destinationId);
            verify(destinationRepository).save(ArgumentMatchers.any(Destination.class));
            verify(destinationMapperImpl).entityToDto(updatedOtherDestinationEntity);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when regular user is not the owner")
        void shouldThrowAccessDeniedException_whenRegularUserIsNotOwner() {
            Long destinationId = otherDestination.getId();
            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(otherDestination));

            assertThatThrownBy(() -> destinationService.updateDestination(destinationId, updateRequest, ownerUserDetail))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("You are not authorized to perform this action on this destination.");

            verify(destinationRepository).findById(destinationId);
            verify(destinationRepository, never()).save(ArgumentMatchers.any(Destination.class));
            verify(destinationMapperImpl, never()).entityToDto(ArgumentMatchers.any(Destination.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UserDetail is missing or invalid")
        void shouldThrowIllegalArgumentException_whenUserDetailsIsInvalid() {
            Long destinationId = ownedDestination.getId();

            assertThatThrownBy(() -> destinationService.updateDestination(destinationId, updateRequest, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User information is missing or invalid");

            verify(destinationRepository, never()).findById(ArgumentMatchers.anyLong());
            verify(destinationRepository, never()).save(ArgumentMatchers.any(Destination.class));
            verify(destinationMapperImpl, never()).entityToDto(ArgumentMatchers.any(Destination.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when Destination ID is not found")
        void shouldThrowEntityNotFoundException_whenDestinationIdNotFound() {
            Long nonExistentId = 999L;
            given(destinationRepository.findById(nonExistentId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> destinationService.updateDestination(nonExistentId, updateRequest, ownerUserDetail))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Destination not found with id " + nonExistentId);

            verify(destinationRepository).findById(nonExistentId);
            verify(destinationRepository, never()).save(ArgumentMatchers.any(Destination.class));
            verify(destinationMapperImpl, never()).entityToDto(ArgumentMatchers.any(Destination.class));
        }
    }

    @Nested
    @DisplayName("deleteDestination(Long id, UserDetail userDetails)")
    class DeleteDestinationTests {
        private User ownerUser;
        private User otherUser;
        private User adminUser;
        private UserDetail ownerUserDetail;
        private UserDetail otherUserDetail;
        private UserDetail adminUserDetail;
        private Destination ownedDestination;
        private Destination otherDestination;

        @BeforeEach
        void setup() {
            ownerUser = User.builder()
                    .id(1L)
                    .username("ownerUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();
            ownerUserDetail = new UserDetail(ownerUser);

            otherUser = User.builder()
                    .id(2L)
                    .username("otherUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_USER")))
                    .build();
            otherUserDetail = new UserDetail(otherUser);

            adminUser = User.builder()
                    .id(3L)
                    .username("adminUser")
                    .password("encoded_password")
                    .roles(Collections.singletonList(createRole("ROLE_ADMIN")))
                    .build();
            adminUserDetail = new UserDetail(adminUser);

            ownedDestination = new Destination(100L, "Honduras", "Roatán", "Isla bonita", "http://roatan.png", ownerUser);
            otherDestination = new Destination(200L, "Guatemala", "Antigua Guatemala", "Ciudad colonial", "http://antigua.png", otherUser);
        }

        @Test
        @DisplayName("Should delete destination successfully when authorized as owner")
        void shouldDeleteDestinationSuccessfully_whenAuthorizedAsOwner() {
            Long destinationId = ownedDestination.getId();
            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(ownedDestination));

            String result = destinationService.deleteDestination(destinationId, ownerUserDetail);

            assertThat(result).isEqualTo("Destination with id " + destinationId + " has been deleted");
            verify(destinationRepository).findById(destinationId);
            verify(destinationRepository).delete(ownedDestination);
        }

        @Test
        @DisplayName("Should delete destination successfully when authorized as admin")
        void shouldDeleteDestinationSuccessfully_whenAuthorizedAsAdmin() {
            Long destinationId = otherDestination.getId();
            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(otherDestination));

            String result = destinationService.deleteDestination(destinationId, adminUserDetail);

            assertThat(result).isEqualTo("Destination with id " + destinationId + " has been deleted");
            verify(destinationRepository).findById(destinationId);
            verify(destinationRepository).delete(otherDestination);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when regular user is not the owner")
        void shouldThrowAccessDeniedException_whenRegularUserIsNotOwner() {
            Long destinationId = otherDestination.getId();
            given(destinationRepository.findById(destinationId)).willReturn(Optional.of(otherDestination));

            assertThatThrownBy(() -> destinationService.deleteDestination(destinationId, ownerUserDetail))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("You are not authorized to perform this action on this destination.");

            verify(destinationRepository).findById(destinationId);
            verify(destinationRepository, never()).delete(ArgumentMatchers.any(Destination.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when UserDetail is missing or invalid")
        void shouldThrowIllegalArgumentException_whenUserDetailsIsInvalid() {
            Long destinationId = ownedDestination.getId();

            assertThatThrownBy(() -> destinationService.deleteDestination(destinationId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User information is missing or invalid");

            verify(destinationRepository, never()).findById(ArgumentMatchers.anyLong());
            verify(destinationRepository, never()).delete(ArgumentMatchers.any(Destination.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when Destination ID is not found")
        void shouldThrowEntityNotFoundException_whenDestinationIdNotFound() {
            Long nonExistentId = 999L;
            given(destinationRepository.findById(nonExistentId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> destinationService.deleteDestination(nonExistentId, ownerUserDetail))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Destination not found with id " + nonExistentId);

            verify(destinationRepository).findById(nonExistentId);
            verify(destinationRepository, never()).delete(ArgumentMatchers.any(Destination.class));
        }
    }
}