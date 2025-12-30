package com.techmate.techmate.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.service.MaterialsService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de Integración para MaterialsService.
 * Este test NO requiere Java 17 porque usa Spring Boot Test en lugar de Mockito inline.
 * Levanta el contexto completo de Spring y usa la base de datos H2 en memoria.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MaterialsServiceIntegrationTest {

    @Autowired(required = false)
    private MaterialsService materialsService;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring carga correctamente
        assertThat(materialsService).isNotNull();
    }

    @Test
    void getAllMaterials_ReturnsListSuccessfully() {
        // Este test verifica que el servicio puede obtener materiales
        var materials = materialsService.getAllMaterials();
        assertThat(materials).isNotNull();
    }
    
    @Test
    void serviceCanBeInjected() {
        // Verifica que todas las dependencias se inyectan correctamente
        assertThat(materialsService).isNotNull();
    }
}

