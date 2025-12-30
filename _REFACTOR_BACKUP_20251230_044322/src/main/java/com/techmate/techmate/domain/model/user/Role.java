package com.techmate.techmate.domain.model.user;

import java.util.Objects;

/**
 * 🎯 DOMAIN MODEL - Role (Pure Java, NO JPA)
 * 
 * Representa un rol en el dominio de negocio.
 * Modelo immutable e independiente de frameworks.
 * 
 * REGLAS DE NEGOCIO:
 * - El nombre del rol es único y obligatorio
 * - Los roles son inmutables (no se modifican después de crear)
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public class Role {

    private final Integer id;
    private final String name;

    private Role(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("El nombre del rol no puede tener más de 50 caracteres");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Verifica si el rol es administrador
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(name);
    }

    /**
     * Verifica si el rol es usuario regular
     */
    public boolean isUser() {
        return "USER".equalsIgnoreCase(name);
    }

    /**
     * Verifica si el rol es moderador
     */
    public boolean isModerator() {
        return "MODERATOR".equalsIgnoreCase(name);
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS (Immutable)
    // ═══════════════════════════════════════════════════════════════════

    public Integer getId() { return id; }
    public String getName() { return name; }

    // ═══════════════════════════════════════════════════════════════════
    // BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════

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
            return new Role(this);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EQUALS, HASHCODE, TOSTRING
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
