package com.Travellers.DreamRoute.services;


import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.models.User;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import com.Travellers.DreamRoute.repositories.UserRepository;
import com.Travellers.DreamRoute.security.UserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
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

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

   User testUser, testUser2;
   UserResponse testUserResponse, testUserResponse2;

   @BeforeEach
    void setUp(){
       testUser = User.builder()
               .id(1L)
               .username("testuser")
               .email("test@dreamroute.com")
               .password("testDeamRoute1!")
               .roles(List.of())
               .build();

       testUser2 = User.builder()
               .id(2L)
               .username("testuser2")
               .email("test2@dreamroute.com")
               .password("testDreamRoute2!")
               .roles(List.of())
               .build();

       testUserResponse = new UserResponse(
               1L,
               "testuser",
               "test@dreamroute.com",
               List.of(),
               List.of("ROLE_USER")
       );

       testUserResponse2 = new UserResponse(
               2L,
               "testuser2",
               "test2@dreamroute.com",
               List.of(),
               List.of("ROLE_ADMIN")


       );
   }

   @Nested
   @DisplayName("GET User by Username")
   class GetUserByUsername{
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

   }

   @Nested
   @DisplayName("Get All Users")
   class GetAllUsers {

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
   }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {
        @Test
        @DisplayName("should delete user by user ID successfully")
        void shouldDeleteUserByUserIdSuccessfully() {
            Long userIdToDelete = testUser.getId();
            UserDetail userDetailTest = new UserDetail(testUser);

            given(userRepository.findById(userIdToDelete)).willReturn(Optional.of(testUser));

            String result = userService.deleteUser(userIdToDelete, userDetailTest);

            assertThat(result).isEqualTo("User with id " + userIdToDelete + " has been deleted");
        }
    }

    @Nested
    @DisplayName("Register User")
    class RegisterUser {
       void shouldRegisterNewUserSuccessfully() {
           UserRequest request = new UserRequest(
                   "newuser",
                   "user@example.com",
                   "NewPassword12345."
           );
           Role defaultRole = new Role(1L, "ROLE_USER", List.of());

           User newUser = User.builder()
                   .username(request.username())
                   .email(request.email())
                   .password(request.password())
                   .roles(List.of(defaultRole))
                   .build();

           given(userRepository.existsByUsername(request.username())).willReturn(false);
           given(userRepository.existsByEmail(request.email())).willReturn(false);
           given(roleRepository.findByRoleNameIgnoreCase("ROLE_USER")).willReturn(Optional.of(defaultRole));
           given(userMapperImpl.dtoToEntity(request, new ArrayList<>(), List.of(defaultRole))).willReturn(newUser);
           given(passwordEncoder.encode(request.password())).willReturn("encodedPassword123");
           given(userMapperImpl.entityToDto(newUser)).willReturn(testUserResponse);

           UserResponse response = userService.registerUser(request);

           assertThat(response).isNotNull();
           assertThat(response.username()).isEqualTo("testuser");
       }

        @Test
        @DisplayName("should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            UserRequest request = new UserRequest(
                    "existinguser",
                    "newuser@example.com",
                    "newPassword123!"
            );

            given(userRepository.existsByUsername(request.username())).willReturn(true);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.registerUser(request);
            });

            assertThat(exception.getMessage()).contains("Username already taken");
        }

        @Test
        @DisplayName("should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            UserRequest request = new UserRequest(
                    "newuser",
                    "existing@example.com",
                    "newPassword123!"
            );

            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.registerUser(request);
            });

            assertThat(exception.getMessage()).contains("Email already registered");
        }
    }
}
