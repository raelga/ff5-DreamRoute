package com.Travellers.DreamRoute.services;

import com.Travellers.DreamRoute.dtos.role.RoleMapperImpl;
import com.Travellers.DreamRoute.dtos.role.RoleResponse;
import com.Travellers.DreamRoute.exceptions.EntityNotFoundException;
import com.Travellers.DreamRoute.models.Role;
import com.Travellers.DreamRoute.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Test")
public class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapperImpl roleMapperImpl;

    @InjectMocks
    RoleService roleService;

    Role role;
    RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_ADMIN", null);
        roleResponse = new RoleResponse(1L, "ROLE_ADMIN");
    }

    @Nested
    @DisplayName("getAllRoles")
    class GetAllRoles {

        @Test
        @DisplayName("getAllRoles")
        void shouldReturnAllRoles() {
            given(roleRepository.findAll()).willReturn(List.of(role));
            given(roleMapperImpl.entityToDto(role)).willReturn(roleResponse);

            List<RoleResponse> result = roleService.getAllRoles();

            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).roleName()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("getRoleById")
    class GetRoleById {

        @Test
        @DisplayName("should return RoleResponse for valid id")
        void shouldReturnRoleResponse() {
            given(roleRepository.findById(1L)).willReturn(Optional.of(role));
            given(roleMapperImpl.entityToDto(role)).willReturn(roleResponse);

            RoleResponse result = roleService.getRoleById(1L);

            assertThat(result).isNotNull();
            assertThat(result.roleName()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("should throw EntityNotFoundException for invalid id")
        void shouldThrowWhenIdNotFound() {
            given(roleRepository.findById(99L)).willReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class, () -> roleService.getRoleById(99L));

            assertThat(exception.getMessage()).contains("Role not found with id 99");
        }
    }
}