package com.techmate.techmate.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * La clase {@code Categories} representa una categoría en el sistema.
 * Esta entidad se mapea a la tabla {@code categories} en la base de datos.
 * 
 * <p>La clase incluye los siguientes atributos:</p>
 * <ul>
 *     <li><b>categoryId:</b> Identificador único de la categoría.</li>
 *     <li><b>name:</b> Nombre de la categoría.</li>
 *     <li><b>imagePath:</b> Ruta de la imagen asociada a la categoría.</li>
 *     <li><b>subCategories:</b> Lista de subcategorías asociadas a esta categoría.</li>
 * </ul>
 * 
 * <p>Además, la clase utiliza Lombok para reducir el código boilerplate mediante
 * las anotaciones {@code @AllArgsConstructor} y {@code @NoArgsConstructor}.</p>
 */

 @Data
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;  // Auto-mapea a id

    @NotBlank(message = "El nombre de la categoría no puede estar vacío.")
    @Size(min = 3, max = 100, message = "El nombre de la categoría debe tener entre 3 y 100 caracteres.")
    @Column(name = "name", unique = true)
    private String name;

    private String imagePath;  // Auto-mapea a image_path

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategories> subCategories;

    // Compatibility getters/setters for legacy code/tests
    public int getCategoryId() { return this.id; }
    public void setCategoryId(int id) { this.id = id; }

    
}










