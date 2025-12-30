package com.techmate.techmate.domain.model.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

/**
 * 🎯 DOMAIN MODEL - User (Pure Java, NO JPA)
 * 
 * Representa un usuario en el dominio de negocio.
 * Modelo immutable e independiente de cualquier framework.
 * 
 * REGLAS DE NEGOCIO:
 * - El nombre de usuario es único y obligatorio
 * - El email es único y obligatorio
 * - La contraseña debe estar hasheada (nunca guardamos plain text)
 * - El usuario puede tener múltiples roles
 * - Un usuario puede estar habilitado o deshabilitado
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public class User {

    private final Integer id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String passwordHash; // SIEMPRE hasheada
    private final LocalDate birthDate;
    private final Gender gender;
    private final boolean enabled;
    private final String profileImageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Set<String> roleNames; // Solo nombres de roles (no objetos)

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.birthDate = builder.birthDate;
        this.gender = builder.gender;
        this.enabled = builder.enabled;
        this.profileImageUrl = builder.profileImageUrl;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.roleNames = builder.roleNames != null ? Collections.unmodifiableSet(builder.roleNames) : Collections.emptySet();
        
        validate();
    }

    private void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña hasheada no puede estar vacía");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Habilita el usuario
     */
    public User enable() {
        return new Builder()
            .id(this.id)
            .username(this.username)
            .firstName(this.firstName)
            .lastName(this.lastName)
            .email(this.email)
            .passwordHash(this.passwordHash)
            .birthDate(this.birthDate)
            .gender(this.gender)
            .enabled(true)
            .profileImageUrl(this.profileImageUrl)
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .roleNames(this.roleNames)
            .build();
    }

    /**
     * Deshabilita el usuario
     */
    public User disable() {
        return new Builder()
            .id(this.id)
            .username(this.username)
            .firstName(this.firstName)
            .lastName(this.lastName)
            .email(this.email)
            .passwordHash(this.passwordHash)
            .birthDate(this.birthDate)
            .gender(this.gender)
            .enabled(false)
            .profileImageUrl(this.profileImageUrl)
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .roleNames(this.roleNames)
            .build();
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        return this.roleNames.contains(roleName);
    }

    /**
     * Verifica si el usuario tiene todos los roles especificados
     */
    public boolean hasAllRoles(Set<String> roles) {
        return this.roleNames.containsAll(roles);
    }

    /**
     * Verifica si el usuario tiene al menos uno de los roles especificados
     */
    public boolean hasAnyRole(Set<String> roles) {
        return this.roleNames.stream().anyMatch(roles::contains);
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS (Immutable)
    // ═══════════════════════════════════════════════════════════════════

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDate getBirthDate() { return birthDate; }
    public Gender getGender() { return gender; }
    public boolean isEnabled() { return enabled; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<String> getRoleNames() { return roleNames; }

    // ═══════════════════════════════════════════════════════════════════
    // BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String passwordHash;
        private LocalDate birthDate;
        private Gender gender;
        private boolean enabled = false;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<String> roleNames;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public Builder gender(Gender gender) { this.gender = gender; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder roleNames(Set<String> roleNames) { this.roleNames = roleNames; return this; }

        public User build() {
            return new User(this);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EQUALS, HASHCODE, TOSTRING
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", roleNames=" + roleNames +
                '}';
    }

    /**
     * Enum para género
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
