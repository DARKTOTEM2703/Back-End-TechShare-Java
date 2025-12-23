package com.techmate.techmate.entity;

import java.util.List;
import java.math.BigDecimal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materials")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
// TEMPORAL: Deshabilitado hasta que migraciones añadan columnas
// public class Materials extends AuditableEntity {
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; // Auto-mapea a id

    @Column(name = "image_path")
    private String imagePath; // Auto-mapea a image_path

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(name = "description")
    private String description;

    @NotNull(message = "El precio no puede ser nulo")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price; // Usar BigDecimal para precisión monetaria

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0") // Permite que el stock sea 0
    @Column(name = "stock")
    private int stock;

    @NotNull(message = "El stock prestable no puede ser nulo")
    @Min(value = 0, message = "El stock prestable debe ser mayor o igual a 0") // Permite que el stock prestable sea 0
    @Column(name = "borrowable_stock")
    private int borrowableStock; // Renombrado a camelCase para seguir convenciones Java

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategories subCategory;

    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleMaterials> roleMaterials;

    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movements> movements;

    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsBorrow> detailsBorrow; // Relación con los detalles de préstamo

    // Compatibility getters
    public int getMaterialsId() {
        return this.id;
    }

    public int getId() {
        return this.id;
    }

    // Compatibility setter used by tests and older code
    public void setMaterialsId(int materialsId) {
        this.id = materialsId;
    }
}
