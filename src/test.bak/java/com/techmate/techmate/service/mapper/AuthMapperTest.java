package com.techmate.techmate.service.mapper;

import static org.junit.jupiter.api.Assertions.*;
import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import org.junit.jupiter.api.Test;

class AuthMapperTest {

    private final AuthMapper mapper = new AuthMapper();

    @Test
    void toEntity_mapsFieldsCorrectly() {
        RegisterRequest req = new RegisterRequest();
        req.setUser_name("jdoe");
        req.setFirst_name("John");
        req.setLast_name("Doe");
        req.setEmail("jdoe@example.com");
        req.setPassword("secret");

        Usuario u = mapper.toEntity(req);

        assertEquals("jdoe", u.getUser_name());
        assertEquals("John", u.getFirst_name());
        assertEquals("Doe", u.getLast_name());
        assertEquals("jdoe@example.com", u.getEmail());
        assertEquals("secret", u.getPassword());
    }
}
