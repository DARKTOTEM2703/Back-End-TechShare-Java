package com.techmate.techmate.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "username", unique = true)
    private String user_name;  // mapea a username
    
    @Column(name = "first_name")
    private String first_name;
    
    @Column(name = "last_name")
    private String last_name;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "is_enabled")
    private boolean isEnabled = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", // Nombre de la tabla intermedia normalizado
        joinColumns = @JoinColumn(name = "user_id"), // Columna que se refiere a Usuario (users.id)
        inverseJoinColumns = @JoinColumn(name = "role_id") // Columna que se refiere a Role (roles.id)
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Movements> movements;

    // TRANSIENT: Relación deshabilitada porque la entidad DetailsBorrow no existe en la BD
    // y causa errores al cargar la tabla 'borrow'. Se puede restaurar cuando se normalice el esquema.
    @Transient
    private List<Borrow> borrows;

    // Enum para género
    public enum Gender {
        Mujer, Hombre, Otro
    }
}


