package com.techmate.techmate.domain.model.material;

import java.util.Objects;

/**
 * 🎯 DOMAIN MODEL - SubCategory (Pure Java, NO JPA)
 * 
 * Representa una subcategoría de materiales en el dominio de negocio.
 * Modelo inmutable e independiente de frameworks.
 * 
 * REGLAS DE NEGOCIO:
 * - El nombre de la subcategoría es obligatorio
 * - Debe pertenecer a una categoría padre válida
 * - La imagen es opcional
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public class SubCategory {

    private final Integer id;
    private final String name;
    private final String imagePath;
    private final Integer categoryId;

    private SubCategory(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.imagePath = builder.imagePath;
        this.categoryId = builder.categoryId;
        
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la subcategoría no puede estar vacío");
        }
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("La subcategoría debe pertenecer a una categoría válida");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Actualiza el nombre de la subcategoría
     */
    public SubCategory updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío");
        }
        
        return new Builder()
            .id(this.id)
            .name(newName)
            .imagePath(this.imagePath)
            .categoryId(this.categoryId)
            .build();
    }

    /**
     * Actualiza la imagen de la subcategoría
     */
    public SubCategory updateImage(String newImagePath) {
        return new Builder()
            .id(this.id)
            .name(this.name)
            .imagePath(newImagePath)
            .categoryId(this.categoryId)
            .build();
    }

    /**
     * Cambia la categoría padre (reasignación)
     */
    public SubCategory reassignToCategory(Integer newCategoryId) {
        if (newCategoryId == null || newCategoryId <= 0) {
            throw new IllegalArgumentException("La nueva categoría debe ser válida");
        }
        
        return new Builder()
            .id(this.id)
            .name(this.name)
            .imagePath(this.imagePath)
            .categoryId(newCategoryId)
            .build();
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS (Immutable)
    // ═══════════════════════════════════════════════════════════════════

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public Integer getCategoryId() { return categoryId; }

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
        private Integer categoryId;

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

        public Builder categoryId(Integer categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public SubCategory build() {
            return new SubCategory(this);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EQUALS, HASHCODE, TOSTRING
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubCategory that = (SubCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
