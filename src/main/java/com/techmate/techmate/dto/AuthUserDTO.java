package com.techmate.techmate.dto;

import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO optimizado para autenticación que evita los problemas de lazy loading 
 * y ConcurrentModificationException de las entidades JPA.
 * 
 * Esta clase contiene solo los datos necesarios para la autenticación
 * sin referencias a entidades con colecciones de Hibernate.
 */
public class AuthUserDTO {
    private Integer id;
    private String username;
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;
    
    private String password;
    private String firstName;
    private String lastName;
    private boolean isEnabled;
    private List<String> roleNames;

    public AuthUserDTO() {}

    public AuthUserDTO(Integer id, String username, String email, String password, 
                      String firstName, String lastName, boolean isEnabled, 
                      List<String> roleNames) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isEnabled = isEnabled;
        this.roleNames = roleNames;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public List<String> getRoleNames() { return roleNames; }
    public void setRoleNames(List<String> roleNames) { this.roleNames = roleNames; }
}