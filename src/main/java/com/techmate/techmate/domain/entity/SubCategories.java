package com.techmate.techmate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sub_categories")
public class SubCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id;  // Auto-mapea a id

    @NotBlank(message = "El nombre no puede estar vacío")
        @Column(name = "name")
        private String name;  // Auto-mapea a 'name'

        @Column(name = "image_path")
        private String imagePath;  // Auto-mapea a 'image_path'

    @NotNull(message = "La categoría no puede ser nula")
    @ManyToOne
    @JoinColumn(name = "category_id")  // Clave foránea que apunta a la categoría
    private Categories category;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materials;

    // Compatibility getters/setters for legacy code/tests
    public int getSubCategoryId() { return this.id; }
    public void setSubCategoryId(int id) { this.id = id; }

}










