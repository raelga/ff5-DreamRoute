package com.Travellers.DreamRoute.services;


import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {
   @Mock
    UserRepository userRepository;

   @Mock
    UserMapperImpl userMapperImpl;

   @InjectMocks
    UserService userService;

   User testUser;
   User testUser2;
   UserResponse testUserResponse;
   UserResponse testUserResponse2;

   @BeforeEach
    void setUp(){
       testUser = User.builder()
               .id(1L)
               .username("testuser")
               .email("test@dreamroute.com")
               .password("testDeamRoute1!")
               .build();

       testUser2 = User.builder()
               .id(2L)
               .username("testuser2")
               .email("test2@dreamroute.com")
               .password("testDreamRoute2!")
               .build();

       testUserResponse = new UserResponse(
               1L,
               "testuser",
               "test@dreamroute.com",
               "testDreamRoute1!", //consider removing password from DTO
               List.of(), //add mock destinations?
               List.of("ROLE_USER")
       );

       testUserResponse2 = new UserResponse(
               2L,
               "testuser2",
               "test2@dreamroute.com",
               "testDreamRoute2!", //consider removing password from DTO
               List.of(), //add mock destinations?
               List.of("ROLE_ADMIN")


       );
   }

   @Test
   @DisplayName("should return UserResponse for a given username")
    void shouldReturnUserResponseForUsername() {
       given(userRepository.findByUsernameIgnoreCase("testuser")).willReturn(Optional.of(testUser));
       given(userMapperImpl.entityToDto(testUser)).willReturn(testUserResponse);

       UserResponse result = userService.getUserByUsername("testuser");

       assertThat(result).isNotNull();
       assertThat(result.id()).isEqualTo(testUserResponse.id());
       assertThat(result.username()).isEqualTo(testUserResponse.username());
       assertThat(result.email()).isEqualTo(testUserResponse.email());
       assertThat(result.roles()).isEqualTo(testUserResponse.roles());
   }

   @Test
   @DisplayName("should throw EntityNotFoundException when user does not exist")
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist(){
       given(userRepository.findByUsernameIgnoreCase("testuserdoesnotexist")).willReturn(Optional.empty());

       Exception exception = assertThrows(EntityNotFoundException.class, () -> {
           userService.getUserByUsername("testuserdoesnotexist");
       });
       assertThat(exception.getMessage()).contains("User not found with username testuserdoesnotexist");
   }

    @Test
    @DisplayName("should return UserResponse for all users")
    void shouldReturnUserResponseForAllUsers() {
        List<User> allUsers = List.of(testUser, testUser2);
        given(userRepository.findAll()).willReturn(allUsers);
        given(userMapperImpl.entityToDto(testUser)).willReturn(testUserResponse);
        given(userMapperImpl.entityToDto(testUser2)).willReturn(testUserResponse2);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).id()).isEqualTo(testUserResponse.id());
        assertThat(result.get(1).id()).isEqualTo(testUserResponse2.id());
        assertThat(result.get(0).username()).isEqualTo(testUserResponse.username());
        assertThat(result.get(1).username()).isEqualTo(testUserResponse2.username());
        assertThat(result.get(0).email()).isEqualTo(testUserResponse.email());
        assertThat(result.get(1).email()).isEqualTo(testUserResponse2.email());
        assertThat(result.get(0).roles()).isEqualTo(testUserResponse.roles());
        assertThat(result.get(1).roles()).isEqualTo(testUserResponse2.roles());
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when no users exist")
    void shouldThrowEntityNotFoundExceptionWhenNoUsersExist(){
        given(userRepository.findAll()).willReturn(Collections.emptyList());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getAllUsers();
        });
        assertThat(exception.getMessage()).contains("No users exist");
    }

}
