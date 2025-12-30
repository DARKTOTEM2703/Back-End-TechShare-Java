package com.techmate.techmate.domain.model.material;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 🎯 DOMAIN MODEL - Material (Pure Java, NO JPA)
 * 
 * Representa un material en el dominio de negocio.
 * Este modelo es independiente de cualquier framework (Spring, JPA, etc.)
 * 
 * REGLAS DE NEGOCIO:
 * - El stock debe ser >= 0
 * - El borrowableStock debe ser <= stock
 * - El precio debe ser > 0
 * - El material debe pertenecer a una subcategoría
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public class Material {

    private final Integer id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stock;
    private final int borrowableStock;
    private final String imagePath;
    private final Integer subCategoryId;

    private Material(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.stock = builder.stock;
        this.borrowableStock = builder.borrowableStock;
        this.imagePath = builder.imagePath;
        this.subCategoryId = builder.subCategoryId;
        
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del material no puede estar vacío");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del material no puede estar vacía");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (borrowableStock < 0) {
            throw new IllegalArgumentException("El stock prestable no puede ser negativo");
        }
        if (borrowableStock > stock) {
            throw new IllegalArgumentException("El stock prestable no puede ser mayor que el stock total");
        }
        if (subCategoryId == null || subCategoryId <= 0) {
            throw new IllegalArgumentException("El material debe tener una subcategoría válida");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC METHODS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Reduce el stock prestable cuando se realiza un préstamo
     */
    public Material reduceBorrowableStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a prestar debe ser positiva");
        }
        if (quantity > borrowableStock) {
            throw new IllegalStateException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                    borrowableStock, quantity));
        }
        
        return new Builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .price(this.price)
            .stock(this.stock)
            .borrowableStock(this.borrowableStock - quantity)
            .imagePath(this.imagePath)
            .subCategoryId(this.subCategoryId)
            .build();
    }

    /**
     * Restaura el stock prestable cuando se devuelve un material
     */
    public Material restoreBorrowableStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a devolver debe ser positiva");
        }
        
        int newBorrowableStock = this.borrowableStock + quantity;
        if (newBorrowableStock > this.stock) {
            throw new IllegalStateException("La devolución excede el stock total del material");
        }
        
        return new Builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .price(this.price)
            .stock(this.stock)
            .borrowableStock(newBorrowableStock)
            .imagePath(this.imagePath)
            .subCategoryId(this.subCategoryId)
            .build();
    }

    /**
     * Incrementa el stock total (compra de nuevos materiales)
     */
    public Material increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a añadir debe ser positiva");
        }
        
        return new Builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .price(this.price)
            .stock(this.stock + quantity)
            .borrowableStock(this.borrowableStock + quantity) // Aumenta también el prestable
            .imagePath(this.imagePath)
            .subCategoryId(this.subCategoryId)
            .build();
    }

    /**
     * Verifica si hay stock disponible para préstamo
     */
    public boolean hasAvailableStock(int quantity) {
        return this.borrowableStock >= quantity;
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS (Immutable)
    // ═══════════════════════════════════════════════════════════════════

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public int getBorrowableStock() { return borrowableStock; }
    public String getImagePath() { return imagePath; }
    public Integer getSubCategoryId() { return subCategoryId; }

    // ═══════════════════════════════════════════════════════════════════
    // BUILDER PATTERN
    // ═══════════════════════════════════════════════════════════════════

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String description;
        private BigDecimal price;
        private int stock;
        private int borrowableStock;
        private String imagePath;
        private Integer subCategoryId;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public Builder borrowableStock(int borrowableStock) {
            this.borrowableStock = borrowableStock;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder subCategoryId(Integer subCategoryId) {
            this.subCategoryId = subCategoryId;
            return this;
        }

        public Material build() {
            return new Material(this);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EQUALS, HASHCODE, TOSTRING
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(id, material.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", borrowableStock=" + borrowableStock +
                ", subCategoryId=" + subCategoryId +
                '}';
    }
}
