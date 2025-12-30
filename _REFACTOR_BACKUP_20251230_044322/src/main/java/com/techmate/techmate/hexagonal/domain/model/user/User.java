package com.techmate.techmate.hexagonal.domain.model.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

/**
 * Domain model for User entity.
 * 
 * Immutable value object representing a user in the system.
 * No Spring/JPA annotations - pure domain logic.
 */
public final class User {

    private final Integer id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String passwordHash;
    private final LocalDate birthDate;
    private final String gender;
    private final boolean enabled;
    private final String profileImageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Set<String> roleNames;

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
        this.roleNames = builder.roleNames;
    }

    // ============= GETTERS =============

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getRoleNames() {
        return Collections.unmodifiableSet(roleNames);
    }

    // ============= BUSINESS LOGIC =============

    /**
     * Get full name (firstName + lastName).
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user has specific role.
     */
    public boolean hasRole(String roleName) {
        if (roleName == null || roleNames == null) {
            return false;
        }
        return roleNames.contains(roleName.toUpperCase());
    }

    /**
     * Check if user has all specified roles.
     */
    public boolean hasAllRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        return roles.stream()
                .allMatch(this::hasRole);
    }

    /**
     * Check if user has any of specified roles.
     */
    public boolean hasAnyRole(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.stream()
                .anyMatch(this::hasRole);
    }

    /**
     * Check if user is admin.
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if user is moderator.
     */
    public boolean isModerator() {
        return hasRole("MODERATOR");
    }

    /**
     * Check if user is regular user.
     */
    public boolean isRegularUser() {
        return hasRole("USER");
    }

    // ============= BUILDER =============

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
        private String gender;
        private boolean enabled;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<String> roleNames = Set.of();

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder profileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder roleNames(Set<String> roleNames) {
            this.roleNames = roleNames != null ? Set.copyOf(roleNames) : Set.of();
            return this;
        }

        public User build() {
            validateUser();
            return new User(this);
        }

        private void validateUser() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("Email format is invalid");
            }
            if (passwordHash == null || passwordHash.trim().isEmpty()) {
                throw new IllegalArgumentException("Password hash is required");
            }
            if (firstName == null || firstName.trim().isEmpty()) {
                throw new IllegalArgumentException("First name is required");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new IllegalArgumentException("Last name is required");
            }
        }

        private boolean isValidEmail(String email) {
            return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
    }
}
