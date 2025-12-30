package com.techmate.techmate.hexagonal.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // Auto-mapea a id

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    @Column(name = "name", unique = true, nullable = false)
    private String name; // mapeado a columna 'name'

    // DESHABILITADO: Relación bidireccional causa ConcurrentModificationException
    // @ManyToMany(mappedBy = "roles")
    // private Set<Usuario> usuarios = new HashSet<>();

    // DESHABILITADO: Esta relación OneToMany puede estar causando ConcurrentModificationException
    // @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<RoleMaterials> roleMaterials;
    
    @Transient
    private List<RoleMaterials> roleMaterials; // Temporal: sin persistencia JPA


    // Compatibility methods for legacy code/tests
    public Integer getRoleId() { return this.id; }
    public void setRoleId(Integer id) { this.id = id; }

    // Spanish-named compatibility
    public String getNombre() { return this.name; }
    public void setNombre(String nombre) { this.name = nombre; }

    // Método para agregar materiales a un rol
   
}





