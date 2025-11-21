package com.techmate.techmate.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;

/**
 * Implementación del servicio de autenticación de usuarios para Spring Security.
 * Esta clase es responsable de cargar los detalles del usuario desde la base de datos
 * y proporcionar una instancia de UserDetails que contiene la información del usuario
 * para el proceso de autenticación y autorización.
 * 
 * Esta clase implementa la interfaz `UserDetailsService` de Spring Security.
 * 
 * Anotaciones:
 * - @Service: Marca esta clase como un componente de servicio, lo que permite que
 *   Spring la gestione y pueda ser inyectada en otras clases.
 * 
 * Dependencias:
 * - UsuarioRepository: Un repositorio para buscar usuarios en la base de datos.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Inyecta el repositorio de usuarios para acceder a la base de datos.
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository; // Inyecta el repositorio de roles

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, UsuarioRoleRepository usuarioRoleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
    }


    /**
     * Carga un usuario desde la base de datos utilizando su email.
     * Este método es utilizado por Spring Security durante el proceso de autenticación.
     *
     * @param email El email del usuario que se está intentando autenticar.
     * @return Una instancia de UserDetails que contiene la información del usuario.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Busca al usuario en la base de datos usando el email proporcionado.
        Usuario usuario = usuarioRepository.findOneByEmail(email)
                // Si no se encuentra el usuario, lanza una excepción indicando que el usuario no existe.
                .orElseThrow(() -> new UsernameNotFoundException("El usuario con email " + email + " no existe"));
        
        // Obtenemos los nombres de roles directamente con query optimizada (evita ConcurrentModificationException)
        List<String> roleNames = usuarioRoleRepository.findRoleNamesByUsuarioId(usuario.getId());

        // Retorna una instancia de UserDetailsImpl que contiene datos primitivos y lista de nombres de roles
        return new UserDetailsImpl(usuario, roleNames);
    }

    public String getUsuarioUsernamById(int usernameId){
        return usuarioRepository.getUsuarioUsernamById(usernameId)
            .map(Usuario::getUser_name)
            .orElse(null);
    }
}



