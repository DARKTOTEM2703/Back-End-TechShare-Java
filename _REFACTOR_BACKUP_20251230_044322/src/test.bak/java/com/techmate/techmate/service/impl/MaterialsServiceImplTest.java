package com.techmate.techmate.service.impl;

import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.service.materials.manager.MaterialsManager;
import com.techmate.techmate.service.materials.query.MaterialsQueryService;com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialsServiceImpl - Tests Unitarios (Facade)")
class MaterialsServiceImplTest {

    @Mock
    private MaterialsQueryService materialsQueryService;

    @Mock
    private MaterialsManager materialsManager;

    @Mock
    private MaterialsRepository materialsRepository; // Necesario solo para paginación y getMaterialsNameById

    @Mock
    private MultipartFile mockImage;

    @InjectMocks
    private MaterialsServiceImpl materialsService;

    private MaterialsDTO testMaterialDTO;

    @BeforeEach
    void setUp() {
        testMaterialDTO = new MaterialsDTO();
        testMaterialDTO.setId(1);
        testMaterialDTO.setName("Arduino UNO");
        testMaterialDTO.setDescription("Placa de desarrollo");
        testMaterialDTO.setPrice(new BigDecimal("15.99"));
        testMaterialDTO.setStock(10);
        testMaterialDTO.setBorrowableStock(10);
        testMaterialDTO.setImagePath("test-image.jpg");
        testMaterialDTO.setSubCategoryId(1);
    }

    @Test
    @DisplayName("getAllMaterials - Delega a QueryService")
    void getAllMaterials_Delegates() {
        when(materialsQueryService.getAllMaterials()).thenReturn(Arrays.asList(testMaterialDTO));

        List<MaterialsDTO> result = materialsService.getAllMaterials();

        assertThat(result).hasSize(1);
        verify(materialsQueryService).getAllMaterials();
    }

    @Test
    @DisplayName("getMaterialsById - Delega a QueryService")
    void getMaterialsById_Delegates() {
        when(materialsQueryService.getById(1)).thenReturn(testMaterialDTO);

        MaterialsDTO result = materialsService.getMaterialsById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Arduino UNO");
        verify(materialsQueryService).getById(1);
    }

    @Test
    @DisplayName("getMaterialsNameById - Delega a Repository")
    void getMaterialsNameById_Delegates() {
        Materials matEntity = new Materials();
        matEntity.setName("Arduino Entity");
        when(materialsRepository.findById(1)).thenReturn(Optional.of(matEntity));

        String name = materialsService.getMaterialsNameById(1);

        assertThat(name).isEqualTo("Arduino Entity");
        verify(materialsRepository).findById(1);
    }

    @Test
    @DisplayName("getAllMaterialsPaginated - Coordina Repository y QueryService")
    void getAllMaterialsPaginated_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Materials matEntity = new Materials();
        matEntity.setId(1);
        Page<Materials> page = new PageImpl<>(Collections.singletonList(matEntity));

        when(materialsRepository.findAll(pageable)).thenReturn(page);
        when(materialsQueryService.getById(1)).thenReturn(testMaterialDTO);

        Page<MaterialsDTO> result = materialsService.getAllMaterialsPaginated(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Arduino UNO");
        verify(materialsRepository).findAll(pageable);
        verify(materialsQueryService).getById(1);
    }

    @Test
    @DisplayName("createMaterials - Delega a Manager")
    void createMaterials_Delegates() {
        when(materialsManager.createMaterials(testMaterialDTO, mockImage)).thenReturn(testMaterialDTO);

        MaterialsDTO result = materialsService.createMaterials(testMaterialDTO, mockImage);

        assertThat(result).isNotNull();
        verify(materialsManager).createMaterials(testMaterialDTO, mockImage);
    }

    @Test
    @DisplayName("updateMaterials - Delega a Manager")
    void updateMaterials_Delegates() {
        when(materialsManager.updateMaterials(1, testMaterialDTO, mockImage)).thenReturn(testMaterialDTO);

        MaterialsDTO result = materialsService.updateMaterials(1, testMaterialDTO, mockImage);

        assertThat(result).isNotNull();
        verify(materialsManager).updateMaterials(1, testMaterialDTO, mockImage);
    }

    @Test
    @DisplayName("deleteMaterials - Delega a Manager")
    void deleteMaterials_Delegates() {
        doNothing().when(materialsManager).deleteMaterials(1);

        materialsService.deleteMaterials(1);

        verify(materialsManager).deleteMaterials(1);
    }
}
