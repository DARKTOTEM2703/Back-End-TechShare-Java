package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmate.techmate.service.MaterialsService;
import com.techmate.techmate.service.materials.mapper.MaterialsMapper;
import com.techmate.techmate.dto.MaterialRequest;
import com.techmate.techmate.dto.MaterialResponse;
import com.techmate.techmate.dto.MaterialsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = com.techmate.techmate.controller.MaterialsController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterialsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaterialsService materialsService;

    @MockitoBean
    private MaterialsMapper materialsMapper;

    @MockitoBean
    private com.techmate.techmate.service.EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMaterialById_returnsMaterialResponse() throws Exception {
        MaterialsDTO dto = new MaterialsDTO();
        dto.setId(1);
        dto.setName("Test Material");

        MaterialResponse resp = new MaterialResponse(1, "img.jpg", "Test Material", "desc", 10.0, 5, 5, 1, "Sub", java.util.List.of("ROLE_USER"));

        when(materialsService.getMaterialsById(1)).thenReturn(dto);
        when(materialsMapper.toResponse(eq(dto), any())).thenReturn(resp);

        mockMvc.perform(get("/admin/materials/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materials_id").value(1))
                .andExpect(jsonPath("$.name").value("Test Material"));
    }

    @Test
    void createMaterial_handlesMultipartAndReturnsCreated() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "dummy".getBytes());

        MaterialRequest req = new MaterialRequest();
        req.setName("New Material");
        req.setStock(10);

        MaterialsDTO reqDto = new MaterialsDTO();
        reqDto.setName("New Material");
        reqDto.setStock(10);
        reqDto.setImagePath("test.jpg");

        MaterialsDTO created = new MaterialsDTO();
        created.setId(2);
        created.setName("New Material");
        created.setImagePath("test.jpg");

        MaterialResponse resp = new MaterialResponse(2, "test.jpg", "New Material", "", 0.0, 10, 10, 1, "Sub", java.util.List.of());

        when(materialsMapper.fromRequest(any(MaterialRequest.class))).thenReturn(reqDto);
        when(materialsService.createMaterials(any(MaterialsDTO.class), any())).thenReturn(created);
        when(materialsMapper.toResponse(eq(created), any())).thenReturn(resp);

        mockMvc.perform(multipart("/admin/materials/create")
                .file(file)
                .param("name", "New Material")
                .param("stock", "10")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.materials_id").value(2))
                .andExpect(jsonPath("$.name").value("New Material"));
    }
}

