package com.techmate.techmate.hexagonal.infrastructure.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techmate.techmate.hexagonal.domain.entity.Usuario;

/**
 * Implementación de la interfaz UserDetails para integrar la autenticación
 * de Spring Security con la entidad Usuario.
 * 
 * Esta clase convierte la información de la entidad Usuario en un formato
 * que Spring Security entiende para realizar la autenticación y autorización.
 * 
 * IMPORTANTE: Esta clase NO almacena la entidad Usuario completa para evitar
 * problemas de lazy loading y ConcurrentModificationException con Hibernate.
 * En su lugar, extrae solo los datos primitivos necesarios para la
 * autenticación.
 */
public class UserDetailsImpl implements UserDetails {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsImpl.class);
    
    // Datos primitivos extraídos del usuario (evita problemas con lazy loading)
    private final Integer userId;
    private final String email;
    private final String password;
    private final String userName;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;
    private final List<String> roleNames; // Lista de nombres de roles del usuario (ej: "ADMIN")

    public UserDetailsImpl(Usuario usuario, List<String> roleNames) {
        // Extraer datos primitivos del usuario para evitar mantener referencia a
        // entidad con lazy collections
        this.userId = usuario.getId();
        this.email = usuario.getEmail();
        this.password = usuario.getPassword();
        this.userName = usuario.getUser_name();
        this.firstName = usuario.getFirst_name();
        this.lastName = usuario.getLast_name();
        this.enabled = usuario.isEnabled();
        this.roleNames = roleNames != null ? roleNames : List.of();
    }

    /**
     * Constructor optimizado que usa solo datos primitivos para evitar 
     * completamente las entidades JPA y sus problemas de colecciones.
     */
    public UserDetailsImpl(Integer userId, String userName, String password, 
                          Boolean isEnabled, String email, List<String> roleNames) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.firstName = null; // No necesario para autenticación
        this.lastName = null;  // No necesario para autenticación
        this.enabled = isEnabled != null ? isEnabled : false;
        this.roleNames = roleNames != null ? roleNames : List.of();
    }

    /**
     * Constructor que acepta AuthUserDTO (RECOMENDADO).
     * Este es el enfoque más limpio y type-safe.
     */
    public UserDetailsImpl(com.techmate.techmate.hexagonal.infrastructure.dto.AuthUserDTO authUser) {
        this.userId = authUser.getId();
        this.email = authUser.getEmail();
        this.password = authUser.getPassword();
        this.userName = authUser.getUsername();
        this.firstName = authUser.getFirstName();
        this.lastName = authUser.getLastName();
        this.enabled = authUser.isEnabled();
        this.roleNames = authUser.getRoleNames() != null ? authUser.getRoleNames() : List.of();
        
        // DEBUG: Verificar que la contraseña llegue correctamente
        log.info("🔑 [DEBUG] UserDetailsImpl constructor - Password recibida: {}", 
                 password != null ? password.substring(0, Math.min(30, password.length())) + "..." : "null");
    }

    /**
     * Método para obtener el objeto Usuario completo.
     * 
     * NOTA: Este método ahora retorna un Usuario básico reconstruido con solo
     * los datos que tenemos, sin colecciones lazy que puedan causar problemas.
     *
     * @return un objeto Usuario básico con los datos de autenticación.
     */
    /**
     * DEPRECATED: Este método creaba entidades Usuario que pueden activar 
     * el persistence context de Hibernate y causar ConcurrentModificationException.
     * Usar getId(), getEmail(), etc. directamente.
     */
    @Deprecated
    public Usuario getUsuario() {
        throw new UnsupportedOperationException(
            "getUsuario() deshabilitado para evitar ConcurrentModificationException. " +
            "Usar getId(), getEmail(), isEnabled() directamente."
        );
    }

    /**
     * Método para obtener el ID del usuario.
     *
     * @return ID del usuario (tipo Integer).
     */
    public Integer getId() {
        return this.userId;
    }

    /**
     * Método para obtener el nombre del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Método para obtener el apellido del usuario.
     *
     * @return Apellido del usuario.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Método para obtener una lista de IDs de roles asociados al usuario.
     *
     * Utiliza el repositorio `UsuarioRoleRepository` para buscar los roles
     * de acuerdo al ID del usuario y mapea cada `UsuarioRole` al `idRole`.
     *
     * @return Lista de IDs de roles asociados.
     */
    public List<Integer> getIdRoles() {
        // No disponemos de los IDs de role directamente aquí; devolvemos lista vacía.
        // Si se requiere el id de roles, cambiar a pasar una estructura que contenga el
        // id.
        return List.of();
    }

    /**
     * Método para obtener los roles del usuario en forma de `GrantedAuthority`.
     *
     * @return Colección de roles (autoridades) asociadas al usuario.
     */
    public Collection<? extends GrantedAuthority> getRoles() {
        return getAuthorities();
    }

    /**
     * Implementación de `getAuthorities()` para Spring Security.
     *
     * Este método convierte cada rol del usuario a un `GrantedAuthority`,
     * necesario para la autorización en Spring Security.
     * 
     * IMPORTANTE: Spring Security espera que los roles tengan el prefijo "ROLE_"
     * cuando se usa .hasRole("ADMIN"). Por eso convertimos "admin" a "ROLE_ADMIN".
     *
     * @return Colección de objetos `GrantedAuthority` basados en los roles del
     *         usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos cada nombre de rol a GrantedAuthority con prefijo ROLE_
        return roleNames.stream()
                .filter(r -> r != null && !r.isBlank())
                .map(r -> {
                    String normalized = r.trim().toUpperCase();
                    if (!normalized.startsWith("ROLE_")) {
                        normalized = "ROLE_" + normalized;
                    }
                    return new SimpleGrantedAuthority(normalized);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario (String).
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Obtiene el nombre de usuario (email en este caso).
     *
     * @return Email del usuario.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Método adicional para obtener el nombre completo del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return this.userName;
    }

    // Otros métodos requeridos por UserDetails para la autenticación.
    @Override
    public boolean isAccountNonExpired() {
        return true; // Devuelve true, asumiendo que la cuenta no expira.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Devuelve true, asumiendo que la cuenta no está bloqueada.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Devuelve true, asumiendo que las credenciales no expiran.
    }

    @Override
    public boolean isEnabled() {
        return this.enabled; // Usa el valor real del campo isEnabled del usuario
    }
}







