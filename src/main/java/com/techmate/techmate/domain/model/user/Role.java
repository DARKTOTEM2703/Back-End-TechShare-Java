package com.techmate.techmate.domain.model.user;

/**
 * Domain model for Role entity.
 * 
 * Immutable value object representing a user role in the system.
 * No Spring/JPA annotations - pure domain logic.
 */
public final class Role {

    private final Integer id;
    private final String name;

    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    // ============= GETTERS =============

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // ============= BUSINESS LOGIC =============

    /**
     * Check if this is an admin role.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(name);
    }

    /**
     * Check if this is a moderator role.
     */
    public boolean isModerator() {
        return "MODERATOR".equalsIgnoreCase(name);
    }

    /**
     * Check if this is a regular user role.
     */
    public boolean isUser() {
        return "USER".equalsIgnoreCase(name);
    }

    // ============= BUILDER =============

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Role build() {
            validateRole();
            return new Role(this);
        }

        private void validateRole() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Role name is required");
            }
        }
    }
}
