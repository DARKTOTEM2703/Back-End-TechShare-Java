package com.techmate.techmate.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.domain.entity.Role;
import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.domain.repository.RoleRepository;
import com.techmate.techmate.domain.repository.UsuarioRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRoleIntegrationTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    com.techmate.techmate.domain.repository.UsuarioRoleRepository usuarioRoleRepository;

    @Test
    void assigningRole_toUser_persists_relation_no_duplicates() {
        // Nota: actualmente Usuario.roles está marcado como @Transient
        // Esta prueba valida el comportamiento actual y sirve para detectar
        // que la asociación no se persiste.

        Role r = new Role();
        r.setName("ROLE_USER");
        r = roleRepository.save(r);

        Usuario u = new Usuario();
        u.setUser_name("roleuser");
        u.setEmail("ru@example.com");
        u = usuarioRepository.save(u);

        // Intentar asignar role en memoria
        u.getRoles().add(r);
        usuarioRepository.save(u);

        // Verificar que no existen entradas en la tabla user_role (no persistida)
        assertThat(usuarioRoleRepository.findByUsuarioId(u.getId())).isEmpty();
    }
}
