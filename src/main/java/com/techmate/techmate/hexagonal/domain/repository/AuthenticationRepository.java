package com.techmate.techmate.hexagonal.domain.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.dto.AuthUserDTO;

import java.util.List;

/**
 * Repository que usa JdbcTemplate directo para autenticación,
 * evitando completamente JPA/Hibernate y el ConcurrentModificationException.
 * 
 * Este repository NO extiende JpaRepository para evitar cualquier
 * interferencia del persistence context de Hibernate.
 */
@Repository
public class AuthenticationRepository {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationRepository.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthenticationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Buscar usuario por email para autenticación usando JDBC directo.
     * Retorna null si no se encuentra el usuario.
     */
    public AuthUserDTO findUserByEmailForAuth(String email) {
        String sql = "SELECT id, username, email, password, first_name, last_name, is_enabled " +
                    "FROM users WHERE email = ?";
        
        try {
            log.info("🔍 [DEBUG] Ejecutando SQL para email: {}", email);
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String password = rs.getString("password");
                log.info("🔐 [DEBUG] Password cargada para {}: {}", email, password.substring(0, Math.min(20, password.length())) + "...");
                return new AuthUserDTO(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    password,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getBoolean("is_enabled"),
                    null // roles se cargan por separado
                );
            }, email);
        } catch (Exception e) {
            log.error("❌ [DEBUG] Error al buscar usuario {}: {}", email, e.getMessage());
            return null;
        }
    }

    /**
     * Obtener nombres de roles para un usuario usando JDBC directo.
     */
    public List<String> findRoleNamesByUserId(Integer userId) {
        String sql = "SELECT r.name FROM user_role ur " +
                    "JOIN roles r ON ur.role_id = r.id " +
                    "WHERE ur.user_id = ?";
        
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }
}
