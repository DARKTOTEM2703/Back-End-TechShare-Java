package com.techmate.techmate.domain.model.material;

import java.util.Objects;

/**
 * 🎯 DOMAIN MODEL - Category (Pure Java, NO JPA)
 * 
 * Representa una categoría de materiales en el dominio de negocio.
 * Modelo inmutable e independiente de frameworks.
 * 
 * REGLAS DE NEGOCIO:
 * - El nombre de la categoría es obligatorio y único
 * - La imagen es opcional
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public class Category {

    private final Integer id;
    private final String name;
    private final String imagePath;

    private Category(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.imagePath = builder.imagePath;
        
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Actualiza el nombre de la categoría
     */
    public Category updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío");
        }
        
        return new Builder()
            .id(this.id)
            .name(newName)
            .imagePath(this.imagePath)
            .build();
    }

    /**
     * Actualiza la imagen de la categoría
     */
    public Category updateImage(String newImagePath) {
        return new Builder()
            .id(this.id)
            .name(this.name)
            .imagePath(newImagePath)
            .build();
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS (Immutable)
    // ═══════════════════════════════════════════════════════════════════

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }

    // ═══════════════════════════════════════════════════════════════════
    // BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String imagePath;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EQUALS, HASHCODE, TOSTRING
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
