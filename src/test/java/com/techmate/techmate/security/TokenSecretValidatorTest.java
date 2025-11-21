package com.techmate.techmate.security;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
public class TokenSecretValidatorTest {

    @Autowired
    private Environment env;

    @Test
    public void validatorDoesNotThrow_onTestProfile() {
        // Ejecutar el bean TokenSecretValidator en el contexto de test no debe lanzar IllegalStateException
        assertThatCode(() -> {
            TokenSecretValidator validator = new TokenSecretValidator(env);
            validator.validate();
        }).doesNotThrowAnyException();
    }
}

