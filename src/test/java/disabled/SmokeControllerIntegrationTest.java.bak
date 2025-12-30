package com.techmate.techmate.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmokeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminMaterialsAll_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/admin/materials/all"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminMovementAll_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/admin/movement/all"))
                .andExpect(status().is2xxSuccessful());
    }
}

