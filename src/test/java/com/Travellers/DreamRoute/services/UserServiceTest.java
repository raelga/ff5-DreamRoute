package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.user.UserMapperImpl;
import com.Travellers.DreamRoute.dtos.user.UserRequest;
import com.Travellers.DreamRoute.dtos.user.UserResponse;
import com.Travellers.DreamRoute.dtos.user.UserUpdateRequest;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

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
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    User testUser, testUser2;
    UserResponse testUserResponse, testUserResponse2;

    private User adminUser;
    private User normalUser;
    private User anotherNormalUser;

    private UserDetail adminUserDetail;
    private UserDetail normalUserDetail;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        userRole = Role.builder().id(10L).roleName("ROLE_USER").build();
        adminRole = Role.builder().id(11L).roleName("ROLE_ADMIN").build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@dreamroute.com")
                .password("encodedTestDreamRoute1!")
                .roles(new ArrayList<>(List.of(userRole)))
                .build();

        testUser2 = User.builder()
                .id(2L)
                .username("testuser2")
                .email("test2@dreamroute.com")
                .password("encodedTestDreamRoute2!")
                .roles(new ArrayList<>(List.of(adminRole)))
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

       adminUser = User.builder()
               .id(100L)
               .username("admin_user_test")
               .email("admin_user@test.com")
               .password("encodedAdminPassword")
               .roles(new ArrayList<>(List.of(adminRole)))
               .build();

       normalUser = User.builder()
               .id(101L)
               .username("normal_user_test")
               .email("normal_user@test.com")
               .password("encodedNormalUserPassword")
               .roles(new ArrayList<>(List.of(userRole)))
               .build();

       anotherNormalUser = User.builder()
               .id(102L)
               .username("another_normal_user_test")
               .email("another_user@test.com")
               .password("encodedAnotherNormalPassword")
               .roles(new ArrayList<>(List.of(userRole)))
               .build();

       adminUserDetail = new UserDetail(adminUser);
       normalUserDetail = new UserDetail(normalUser);
   }



    @Nested
    @DisplayName("GET User by Username")
    class GetUserByUsername {
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
        void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
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
        @DisplayName("should delete user by user ID successfully when user is admin")
        void shouldDeleteUserByUserIdSuccessfullyWhenUserIsAdmin() {
            Long userIdToDelete = testUser.getId();
            UserDetail userDetailTest = adminUserDetail;

            given(userRepository.findById(userIdToDelete)).willReturn(Optional.of(testUser));

            String result = userService.deleteUser(userIdToDelete, userDetailTest);

            assertThat(result).isEqualTo("User with id " + userIdToDelete + " has been deleted");
            verify(userRepository, times(1)).findById(userIdToDelete);
            verify(userRepository, times(1)).delete(testUser);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user to delete does not exist")
        void shouldThrowEntityNotFoundExceptionWhenUserToDeleteDoesNotExist() {
            Long nonExistentId = 99L;
            UserDetail userDetailTest = adminUserDetail;

            given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

            EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                    userService.deleteUser(nonExistentId, userDetailTest));

            assertThat(thrown.getMessage()).contains("User not found with id " + nonExistentId);
            verify(userRepository, times(1)).findById(nonExistentId);
            verify(userRepository, never()).deleteById(org.mockito.ArgumentMatchers.anyLong());
            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when non-admin user tries to delete a user")
        void shouldNotAllowDeleteUserByUserIdWhenUserIsNotAdmin() {
            Long userIdToDelete = testUser2.getId();
            UserDetail userDetailTest = new UserDetail(testUser);

            AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
                userService.deleteUser(userIdToDelete, userDetailTest);
            });

            assertThat(thrown.getMessage()).contains("Only administrators can delete users");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when admin attempts to delete non-existent user")
        void shouldThrowEntityNotFoundExceptionWhenAdminTriesToDeleteNonExistentUser() {
            Long nonExistentUserId = 99L;
            UserDetail adminUserDetail = new UserDetail(testUser2);

            given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

            EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
                userService.deleteUser(nonExistentUserId, adminUserDetail);
            });

            assertThat(thrown.getMessage()).contains("User not found with id " + nonExistentUserId);

        }
    }

    @Nested
    @DisplayName("Register User")
    class RegisterUser {
        @Test
        @DisplayName("should register new user successfully")
        void shouldRegisterNewUserSuccessfully() {
            UserRequest request = new UserRequest(
                    "newuser",
                    "user@example.com",
                    "NewPassword12345."
            );
            Role defaultRole = new Role(1L, "ROLE_USER", new ArrayList<>());

            User newUserMocked = User.builder()
                    .id(null)
                    .username(request.username())
                    .email(request.email())
                    .password(request.password())
                    .roles(new ArrayList<>(List.of(defaultRole)))
                    .build();

            given(userMapperImpl.dtoToEntity(
                    org.mockito.ArgumentMatchers.any(UserRequest.class),
                    org.mockito.ArgumentMatchers.any(List.class),
                    org.mockito.ArgumentMatchers.any(List.class)
            )).willReturn(newUserMocked);

            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(roleRepository.findByRoleNameIgnoreCase("ROLE_USER")).willReturn(Optional.of(defaultRole));
            given(passwordEncoder.encode(request.password())).willReturn("encodedPassword123");

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                userToSave.setId(1L);
                return userToSave;
            });

            UserResponse expectedUserResponse = new UserResponse(
                    1L,
                    newUserMocked.getUsername(),
                    newUserMocked.getEmail(),
                    Collections.emptyList(),
                    List.of("ROLE_USER")
            );
            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willReturn(expectedUserResponse);

            UserResponse response = userService.registerUser(request);

            assertThat(response).isNotNull();
            assertThat(response.username()).isEqualTo(newUserMocked.getUsername());
            assertThat(response.email()).isEqualTo(newUserMocked.getEmail());
            assertThat(response.roles()).containsExactly("ROLE_USER");

            verify(userRepository, times(1)).existsByUsername(request.username());
            verify(userRepository, times(1)).existsByEmail(request.email());
            verify(roleRepository, times(1)).findByRoleNameIgnoreCase("ROLE_USER");
            verify(userMapperImpl, times(1)).dtoToEntity(
                    org.mockito.ArgumentMatchers.any(UserRequest.class),
                    org.mockito.ArgumentMatchers.any(List.class),
                    org.mockito.ArgumentMatchers.any(List.class)
            );
            verify(passwordEncoder, times(1)).encode(request.password());
            verify(userRepository, times(1)).save(org.mockito.ArgumentMatchers.any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(org.mockito.ArgumentMatchers.any(User.class));
        }

        @Test
        @DisplayName("should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            UserRequest request = new UserRequest(
                    "existinguser",
                    "newuser@example.com",
                    "newPassword123#"
            );

            given(userRepository.existsByUsername(request.username())).willReturn(true);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.registerUser(request);
            });

            assertThat(exception.getMessage()).contains("Username already taken");
            verify(userRepository, times(1)).existsByUsername(request.username());
            verify(userRepository, never()).existsByEmail(org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            UserRequest request = new UserRequest(
                    "newuser",
                    "existing@example.com",
                    "newPassword123#"
            );

            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.registerUser(request);
            });

            assertThat(exception.getMessage()).contains("Email already registered");
            verify(userRepository, times(1)).existsByUsername(request.username());
            verify(userRepository, times(1)).existsByEmail(request.email());
            verify(roleRepository, never()).findByRoleNameIgnoreCase(org.mockito.ArgumentMatchers.anyString());
        }
    }

    @Nested
    @DisplayName("Update User Method")
    class UpdateUserTests {
        @Test
        @DisplayName("should throw EntityNotFoundException if user to update does not exist")
        void updateUser_throwsEntityNotFoundException_ifUserDoesNotExist() {
            Long nonExistentId = 99L;
            UserUpdateRequest userRequest = new UserUpdateRequest("newUsername", "newemail@test.com", null, List.of());
            given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

            EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                    userService.updateUser(nonExistentId, userRequest, adminUserDetail));

            assertThat(thrown.getMessage()).isEqualTo("User not found with id " + nonExistentId);
            verify(userRepository, times(1)).findById(nonExistentId);
            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
        }


        @Test
        @DisplayName("should throw AccessDeniedException if not admin and not owner")
        void updateUser_throwsAccessDeniedException_ifNotAdminAndNotOwner() {
            Long targetUserId = anotherNormalUser.getId();
            UserUpdateRequest userRequest = new UserUpdateRequest("newUsername", "newemail@test.com", null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(anotherNormalUser));

            AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () ->
                    userService.updateUser(targetUserId, userRequest, normalUserDetail));

            assertThat(thrown.getMessage()).isEqualTo("You don't have permission to update this user");
            verify(userRepository, times(1)).findById(targetUserId);
            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
        }
    }

    @Nested
    @DisplayName("Admin user update scenarios")
    class AdminUpdateTests {
        @Test
        @DisplayName("Admin should be able to update their own username and email")
        void adminCanUpdateOwnUsernameAndEmail() {
            Long targetUserId = adminUser.getId();
            String newUsername = "updatedAdminUsername";
            String newEmail = "updated_admin@test.com";
            UserUpdateRequest userRequest = new UserUpdateRequest(newUsername, newEmail, null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(adminUser));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });


            UserResponse result = userService.updateUser(targetUserId, userRequest, adminUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(newUsername);
            assertThat(result.email()).isEqualTo(newEmail);
            assertThat(result.id()).isEqualTo(adminUser.getId());

            verify(userRepository, times(1)).findById(targetUserId);
            verify(userRepository, times(1)).save(any(User.class));
            verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Admin should be able to update another user's username and email")
        void adminCanUpdateAnotherUserUsernameAndEmail() {
            Long targetUserId = anotherNormalUser.getId();
            String newUsername = "updatedAnotherUsername";
            String newEmail = "updated_another@test.com";
            UserUpdateRequest userRequest = new UserUpdateRequest(newUsername, newEmail, null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(anotherNormalUser));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, adminUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(newUsername);
            assertThat(result.email()).isEqualTo(newEmail);
            assertThat(result.id()).isEqualTo(anotherNormalUser.getId());

            verify(userRepository, times(1)).findById(targetUserId);
            verify(userRepository, times(1)).save(any(User.class));
            verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Admin should be able to change another user's roles to an existing role")
        void adminCanChangeAnotherUserRoles_toExistingRole() {
            Long targetUserId = anotherNormalUser.getId();
            List<String> newRoles = List.of("ROLE_ADMIN");
            UserUpdateRequest userRequest = new UserUpdateRequest(anotherNormalUser.getUsername(), anotherNormalUser.getEmail(), null, newRoles);

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(anotherNormalUser));
            given(roleRepository.findByRoleNameIgnoreCase("ROLE_ADMIN")).willReturn(Optional.of(adminRole));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, adminUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.roles()).containsExactly("ROLE_ADMIN");

            verify(userRepository, times(1)).findById(targetUserId);
            verify(roleRepository, times(1)).findByRoleNameIgnoreCase("ROLE_ADMIN");
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Admin should throw RuntimeException if trying to assign a non-existent role")
        void adminThrowsRuntimeException_onNonExistentRole() {
            Long targetUserId = anotherNormalUser.getId();
            List<String> newRoles = List.of("ROLE_NON_EXISTENT");
            UserUpdateRequest userRequest = new UserUpdateRequest(anotherNormalUser.getUsername(), anotherNormalUser.getEmail(), null, newRoles);

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(anotherNormalUser));
            given(roleRepository.findByRoleNameIgnoreCase("ROLE_NON_EXISTENT")).willReturn(Optional.empty());

            RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                    userService.updateUser(targetUserId, userRequest, adminUserDetail));

            assertThat(thrown.getMessage()).isEqualTo("Role not found: ROLE_NON_EXISTENT");
            verify(userRepository, times(1)).findById(targetUserId);
            verify(roleRepository, times(1)).findByRoleNameIgnoreCase("ROLE_NON_EXISTENT");
            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
        }

        @Test
        @DisplayName("Admin should NOT be allowed to change another user's password")
        void adminCannotChangeAnotherUserPassword() {
            Long targetUserId = anotherNormalUser.getId();
            String newPassword = "newEncodedPassword.";
            UserUpdateRequest userRequest = new UserUpdateRequest(anotherNormalUser.getUsername(), anotherNormalUser.getEmail(), newPassword, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(anotherNormalUser));

            AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () ->
                    userService.updateUser(targetUserId, userRequest, adminUserDetail));

            assertThat(thrown.getMessage()).isEqualTo("Admins are not allowed to change passwords of other users");
            verify(userRepository, times(1)).findById(targetUserId);
            verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
        }

        @Test
        @DisplayName("Admin should be allowed to change their own password")
        void adminCanChangeOwnPassword() {
            Long targetUserId = adminUser.getId();
            String rawNewPassword = "newAdminPassword.";
            String encodedNewPassword = "encodedNewAdminPassword";
            UserUpdateRequest userRequest = new UserUpdateRequest(adminUser.getUsername(), adminUser.getEmail(), rawNewPassword, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(adminUser));
            given(passwordEncoder.encode(rawNewPassword)).willReturn(encodedNewPassword);

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, adminUserDetail);

            assertThat(result).isNotNull();

            verify(userRepository, times(1)).findById(targetUserId);
            verify(passwordEncoder, times(1)).encode(rawNewPassword);
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("Owner user update scenarios (non-admin)")
    class OwnerUpdateTests {

        @Test
        @DisplayName("Owner should be able to update their own username, email and password")
        void ownerCanUpdateOwnUsernameEmailAndPassword() {
            assertThat(passwordEncoder).isNotNull();

            Long targetUserId = normalUser.getId();
            String newUsername = "updatedNormalUser";
            String newEmail = "updated_normal_user@test.com";
            String rawNewPassword = "newNormalUserPassword.";
            String encodedNewPassword = "encodedNewNormalUserPassword";
            UserUpdateRequest userRequest = new UserUpdateRequest(newUsername, newEmail, rawNewPassword, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(normalUser));
            given(passwordEncoder.encode(rawNewPassword)).willReturn(encodedNewPassword);


            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, normalUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(newUsername);
            assertThat(result.email()).isEqualTo(newEmail);

            verify(userRepository, times(1)).findById(targetUserId);
            verify(passwordEncoder, times(1)).encode(rawNewPassword);
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Owner should NOT be allowed to change their own roles")
        void ownerCannotChangeOwnRoles() {
            Long targetUserId = normalUser.getId();
            List<String> rolesToAssign = List.of("ROLE_ADMIN");
            UserUpdateRequest userRequest = new UserUpdateRequest(normalUser.getUsername(), normalUser.getEmail(), null, rolesToAssign);

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(normalUser));

            AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () ->
                    userService.updateUser(targetUserId, userRequest, normalUserDetail));

            assertThat(thrown.getMessage()).isEqualTo("Users are not allowed to change their own roles");
            verify(userRepository, times(1)).findById(targetUserId);
            verify(roleRepository, never()).findByRoleNameIgnoreCase(org.mockito.ArgumentMatchers.anyString());
            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
        }

        @Test
        @DisplayName("Owner should not update username if username in request is null or blank")
        void ownerDoesNotUpdateUsername_ifNullOrBlankInRequest() {
            Long targetUserId = normalUser.getId();
            String originalUsername = normalUser.getUsername();
            UserUpdateRequest userRequest = new UserUpdateRequest(null, normalUser.getEmail(), null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(normalUser));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, normalUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(originalUsername);

            verify(userRepository, times(1)).findById(targetUserId);
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Owner should not update email if email in request is null or blank")
        void ownerDoesNotUpdateEmail_ifNullOrBlankInRequest() {
            Long targetUserId = normalUser.getId();
            String originalEmail = normalUser.getEmail();
            UserUpdateRequest userRequest = new UserUpdateRequest(normalUser.getUsername(), "", null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(normalUser));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });

            UserResponse result = userService.updateUser(targetUserId, userRequest, normalUserDetail);

            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(originalEmail);

            verify(userRepository, times(1)).findById(targetUserId);
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }

        @Test
        @DisplayName("Owner should not update password if password in request is null or blank")
        void ownerDoesNotUpdatePassword_ifNullOrBlankInRequest() {
            Long targetUserId = normalUser.getId();
            String originalPassword = normalUser.getPassword();
            UserUpdateRequest userRequest = new UserUpdateRequest(normalUser.getUsername(), normalUser.getEmail(), null, List.of());

            given(userRepository.findById(targetUserId)).willReturn(Optional.of(normalUser));

            given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                return userToSave;
            });

            given(userMapperImpl.entityToDto(org.mockito.ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
                User userEntity = invocation.getArgument(0);
                List<String> roleNames = userEntity.getRoles() != null ?
                        userEntity.getRoles().stream()
                                .map(Role::getRoleName)
                                .collect(Collectors.toCollection(ArrayList::new)) :
                        Collections.emptyList();
                return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), Collections.emptyList(), roleNames);
            });


            UserResponse result = userService.updateUser(targetUserId, userRequest, normalUserDetail);

            assertThat(result).isNotNull();

            verify(userRepository, times(1)).findById(targetUserId);
            verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapperImpl, times(1)).entityToDto(any(User.class));
        }
    }
}