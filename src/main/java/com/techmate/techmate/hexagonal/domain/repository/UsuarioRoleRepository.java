package com.techmate.techmate.hexagonal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.hexagonal.domain.entity.Role;
import com.techmate.techmate.hexagonal.domain.entity.UsuarioRole;

import java.util.List;

@Repository
public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Integer> {
    // Método para obtener los roles asociados con un usuario por su id
    List<UsuarioRole> findByUsuarioId(Integer usuarioId);


    @Query("SELECT ur FROM UsuarioRole ur WHERE ur.usuario.id IN :usuarioIds")
    List<UsuarioRole> findByUsuarioIds(@Param("usuarioIds") List<Integer> usuarioIds);

    List<UsuarioRole> findByRole(Role role);
    
    // Query nativa SQL optimizada para evitar ConcurrentModificationException
    @Query(value = "SELECT r.name FROM user_role ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = :usuarioId", nativeQuery = true)
    List<String> findRoleNamesByUsuarioId(@Param("usuarioId") Integer usuarioId);
}










