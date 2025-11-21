package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    @NonNull
    Optional<Usuario> findById(@NonNull Integer id);  // Método para buscar el usuario por ID
    
    Optional <Usuario> getUsuarioUsernamById(int usernameId);
    
    // Query simple sin JOIN FETCH para evitar ConcurrentModificationException
    // Los roles se cargan por separado en UserDetailsServiceImpl
    Optional<Usuario> findOneByEmail(String email);

    Optional<Usuario> findByEmail(String email);
    
    // Query nativa para activar usuario sin cargar relaciones complejas
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET is_enabled = TRUE WHERE id = :userId", nativeQuery = true)
    void enableUserById(@Param("userId") Integer userId);

}



