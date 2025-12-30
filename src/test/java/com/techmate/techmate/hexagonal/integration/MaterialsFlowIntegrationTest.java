package com.techmate.techmate.hexagonal.integration;

import com.techmate.techmate.hexagonal.application.port.output.ImageStoragePort;
import com.techmate.techmate.hexagonal.application.port.output.MaterialRepositoryPort;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=minio")
@AutoConfigureMockMvc(addFilters = false)
public class MaterialsFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageStoragePort imageStoragePort;

    @MockBean
    private MaterialRepositoryPort materialRepositoryPort;

    @Test
    public void createMaterial_flow_uploadsImage_and_savesMaterial() throws Exception {
        // Mock upload
        when(imageStoragePort.saveImage(any(), any())).thenReturn("uploaded-key.jpg");

        // Mock repository save to return an entity with id
        Materials saved = new Materials();
        saved.setMaterialsId(123);
        saved.setName("Test Material");
        saved.setDescription("desc");
        saved.setPrice(BigDecimal.valueOf(10.0));
        saved.setStock(5);
        when(materialRepositoryPort.save(any())).thenReturn(saved);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image".getBytes());

        mockMvc.perform(multipart("/api/materials/create")
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "Test Material")
                .param("description", "desc")
                .param("stock", "5")
                .param("price", "10.0")
                .param("subCategoryId", "1"))
                .andExpect(status().isCreated());

        verify(imageStoragePort).saveImage(any(), any());
        verify(materialRepositoryPort).save(any());
    }
}
