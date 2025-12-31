package com.techmate.techmate.mapper;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.domain.model.user.User;
import com.techmate.techmate.domain.model.user.Role;
import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.domain.entity.Usuario.Gender;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Maps between domain models and JPA entities for User/Auth module.
 * 
 * Handles conversion of:
 * - User <-> Usuario (JPA entity)
 * - Role <-> Role JPA entity
 * 
 * Note: Manual mapping approach (not MapStruct) to maintain full control
 * over domain model immutability and field transformations.
 */
@Component
public class DomainUserMapper {

    private static final Logger logger = LoggerFactory.getLogger(DomainUserMapper.class);

    /**
     * Converts domain User to JPA Usuario entity.
     */
    public Usuario toEntity(User user) {
        if (user == null) {
            return null;
        }

        logger.debug("Mapping domain User to JPA Usuario: {}", user.getUsername());

        Usuario entity = new Usuario();
        entity.setId(user.getId());
        entity.setUser_name(user.getUsername()); // Column 'username' -> field 'user_name'
        entity.setFirst_name(user.getFirstName());
        entity.setLast_name(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPasswordHash());
        entity.setBirthDate(user.getBirthDate());

        // Map Gender enum
        if (user.getGender() != null) {
            try {
                entity.setGender(Gender.valueOf(user.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid gender value: {}", user.getGender());
                entity.setGender(null);
            }
        }

        entity.setEnabled(user.isEnabled());
        entity.setProfile_image_url(user.getProfileImageUrl());
        entity.setCreated_at(user.getCreatedAt());
        entity.setUpdated_at(user.getUpdatedAt());

        return entity;
    }

    /**
     * Converts JPA Usuario entity to domain User.
     */
    public User toDomain(Usuario entity) {
        if (entity == null) {
            return null;
        }

        logger.debug("Mapping JPA Usuario to domain User: {}", entity.getUser_name());

        return User.builder()
                .id(entity.getId())
                .username(entity.getUser_name()) // Field 'user_name' -> 'username'
                .firstName(entity.getFirst_name())
                .lastName(entity.getLast_name())
                .email(entity.getEmail())
                .passwordHash(entity.getPassword())
                .birthDate(entity.getBirthDate())
                .gender(entity.getGender() != null ? entity.getGender().name().toLowerCase() : null)
                .enabled(entity.isEnabled())
                .profileImageUrl(entity.getProfile_image_url())
                .createdAt(entity.getCreated_at())
                .updatedAt(entity.getUpdated_at())
                .roleNames(extractRoleNames(entity))
                .build();
    }

    /**
     * Converts domain Role to JPA Role entity.
     */
    public com.techmate.techmate.domain.entity.Role roleToEntity(Role role) {
        if (role == null) {
            return null;
        }

        logger.debug("Mapping domain Role to JPA Role: {}", role.getName());

        com.techmate.techmate.domain.entity.Role entity = new com.techmate.techmate.domain.entity.Role();
        entity.setId(role.getId());
        entity.setName(role.getName());

        return entity;
    }

    /**
     * Converts JPA Role entity to domain Role.
     */
    public Role roleToDomain(com.techmate.techmate.domain.entity.Role entity) {
        if (entity == null) {
            return null;
        }

        logger.debug("Mapping JPA Role to domain Role: {}", entity.getName());

        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    /**
     * Extracts role names from Usuario entity.
     * 
     * Note: In current schema, roles are stored separately in user_role join table.
     * This method will need adjustment once UsuarioRepository has role queries.
     */
    private java.util.Set<String> extractRoleNames(Usuario entity) {
        // TODO: Implement role extraction from user_role join table
        // For now, returning empty set - will be populated by repository queries
        return java.util.Set.of();
    }
}
