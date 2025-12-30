package com.techmate.techmate.mapper;

import com.techmate.techmate.dto.UsuarioDTO;
// FIXME: UserRegistrationDTO no existe - comentado temporalmente
// import com.techmate.techmate.dto.UserRegistrationDTO;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.Role;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Usuario (User) domain.
 * Handles all Usuario Entity <-> DTO conversions.
 * 
 * PRODUCTION-READY: Consistent naming, role mapping, and security-aware.
 * 
 * @author TechShare Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // ═══════════════════════════════════════════════════════════════════
    // ENTITY <-> DTO (Internal)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Entity -> DTO
     * Maps Usuario entity to DTO, handling field name differences
     * Password is explicitly ignored for security
     */
    @Mapping(target = "userName", source = "username") // Usuario.username -> UsuarioDTO.userName
    @Mapping(target = "firstName", source = "first_name")
    @Mapping(target = "lastName", source = "last_name")
    @Mapping(target = "profileImageUrl", source = "profile_image_url")
    @Mapping(target = "createdAt", source = "created_at")
    @Mapping(target = "updatedAt", source = "updated_at")
    @Mapping(target = "roles", expression = "java(mapRolesToStrings(usuario.getRoles()))")
    @Mapping(target = "gender", expression = "java(usuario.getGender() != null ? usuario.getGender().name() : null)")
    UsuarioDTO toDTO(Usuario usuario);

    List<UsuarioDTO> toDTOList(List<Usuario> usuarios);

    /**
     * DTO -> Entity (for updates)
     * Password must be handled separately by service (encrypted)
     * Roles must be loaded by service from repository
     */
    @Mapping(target = "username", source = "userName") // UsuarioDTO.userName -> Usuario.username
    @Mapping(target = "first_name", source = "firstName")
    @Mapping(target = "last_name", source = "lastName")
    @Mapping(target = "profile_image_url", source = "profileImageUrl")
    @Mapping(target = "created_at", source = "createdAt")
    @Mapping(target = "updated_at", source = "updatedAt")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true) // Service will set roles
    @Mapping(target = "gender", ignore = true) // Service will parse and set
    Usuario toEntity(UsuarioDTO usuarioDTO);

    // ═══════════════════════════════════════════════════════════════════
    // REGISTRATION DTO
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Registration DTO -> Entity (for new users)
     * Service will:
     * - Encrypt password
     * - Set default roles
     * - Set enabled status
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "userName")
    @Mapping(target = "first_name", source = "firstName")
    @Mapping(target = "last_name", source = "lastName")
    // FIXME: UserRegistrationDTO no existe - comentado temporalmente
    /*
     * @Mapping(target = "password", ignore = true) // Service will encrypt
     * 
     * @Mapping(target = "roles", ignore = true)
     * 
     * @Mapping(target = "isEnabled", constant = "false")
     * 
     * @Mapping(target = "profile_image_url", ignore = true)
     * 
     * @Mapping(target = "created_at", expression =
     * "java(java.time.LocalDateTime.now())")
     * 
     * @Mapping(target = "updated_at", expression =
     * "java(java.time.LocalDateTime.now())")
     * 
     * @Mapping(target = "gender", ignore = true)
     * Usuario fromRegistrationDTO(UserRegistrationDTO registrationDTO);
     */

    // ═══════════════════════════════════════════════════════════════════
    // HELPER METHODS (Role mapping)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Maps Set<Role> to Set<String> for DTO
     * Handles null safety
     */
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Maps Set<String> to Set<Role> for Entity
     * Note: Service layer must load actual Role entities from DB
     */
    default Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of();
        }
        // Service will replace these with actual entities
        return roleNames.stream()
                .map(name -> {
                    Role role = new Role();
                    role.setName(name);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
