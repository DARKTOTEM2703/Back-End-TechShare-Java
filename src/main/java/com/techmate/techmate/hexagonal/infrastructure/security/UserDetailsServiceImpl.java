package com.techmate.techmate.hexagonal.infrastructure.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.infrastructure.dto.AuthUserDTO;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRepository;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRoleRepository;
import com.techmate.techmate.hexagonal.domain.repository.AuthenticationRepository;

/**
 * Implementación del servicio de autenticación de usuarios para Spring
 * Security.
 * Esta clase es responsable de cargar los detalles del usuario desde la base de
 * datos
 * y proporcionar una instancia de UserDetails que contiene la información del
 * usuario
 * para el proceso de autenticación y autorización.
 * 
 * Esta clase implementa la interfaz `UserDetailsService` de Spring Security.
 * 
 * Anotaciones:
 * - @Service: Marca esta clase como un componente de servicio, lo que permite
 * que
 * Spring la gestione y pueda ser inyectada en otras clases.
 * 
 * Dependencias:
 * - UsuarioRepository: Un repositorio para buscar usuarios en la base de datos.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    // Inyecta el repositorio de usuarios para acceder a la base de datos.
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository; // Inyecta el repositorio de roles
    private final AuthenticationRepository authenticationRepository; // Repository sin JPA

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, 
                                UsuarioRoleRepository usuarioRoleRepository,
                                AuthenticationRepository authenticationRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Carga un usuario desde la base de datos utilizando su email.
     * Este método es utilizado por Spring Security durante el proceso de
     * autenticación.
     *
     * @param email El email del usuario que se está intentando autenticar.
     * @return Una instancia de UserDetails que contiene la información del usuario.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email
     *                                   proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("🔍 [DEBUG] UserDetailsService.loadUserByUsername llamado con email: {}", email);
        
        // ENFOQUE SIN JPA: Usar JdbcTemplate directo para evitar ConcurrentModificationException
        log.info("📞 [DEBUG] Llamando a authenticationRepository.findUserByEmailForAuth...");
        AuthUserDTO authUser = authenticationRepository.findUserByEmailForAuth(email);
        log.info("📋 [DEBUG] authenticationRepository retornó: {}", authUser != null ? "usuario encontrado" : "null");
        
        if (authUser == null) {
            log.warn("🚫 [DEBUG] Usuario no encontrado: {}", email);
            throw new UsernameNotFoundException("El usuario con email " + email + " no existe");
        }

        log.info("👤 [DEBUG] Usuario encontrado: {} (ID: {})", email, authUser.getId());

        // Obtener roles usando JdbcTemplate directo
        List<String> roleNames = authenticationRepository.findRoleNamesByUserId(authUser.getId());
        authUser.setRoleNames(roleNames);

        log.info("🎭 [DEBUG] Usuario {} cargado con roles: {}", email, roleNames);
        
        return new UserDetailsImpl(authUser);
    }

    public String getUsuarioUsernamById(int usernameId) {
        return usuarioRepository.getUsuarioUsernamById(usernameId)
                .map(Usuario::getUser_name)
                .orElse(null);
    }
}


