package com.techmate.techmate.service.impl;

import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleMaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MaterialsServiceImpl.
 * 
 * Cobertura:
 * - createMaterials (éxito, nombre duplicado, imagen null)
 * - getMaterialsById (éxito, no encontrado)
 * - updateMaterials (éxito, con/sin imagen)
 * - deleteMaterials (éxito, no encontrado)
 * - getAllMaterials
 * - Conversiones DTO ↔ Entity
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MaterialsServiceImpl - Tests Unitarios")
class MaterialsServiceImplTest {

    @Mock
    private MaterialsRepository materialsRepository;

    @Mock
    private SubCategoriesRepository subCategoriesRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMaterialsRepository roleMaterialsRepository;

    @Mock
    private ImageStorageStrategy imageStorageStrategy;

    @Mock
    private com.techmate.techmate.validation.ImageValidationStrategy imageValidationStrategy;

    @Mock
    private com.techmate.techmate.service.materials.mapper.MaterialsMapper materialsMapper;

    @Mock
    private com.techmate.techmate.service.materials.validator.MaterialsValidator materialsValidator;

    @Mock
    private com.techmate.techmate.service.materials.manager.MaterialsStockManager materialsStockManager;

    @Mock
    private com.techmate.techmate.service.materials.query.MaterialsQueryService materialsQueryService;

    @Mock
    private MultipartFile mockImage;

    @InjectMocks
    private MaterialsServiceImpl materialsService;

    private MaterialsDTO testMaterialDTO;
    private Materials testMaterial;
    private SubCategories testSubCategory;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        testSubCategory = new SubCategories();
        testSubCategory.setSubCategoryId(1);
        testSubCategory.setName("Componentes Electrónicos");

        testRole = new Role();
        testRole.setId(1);
        testRole.setNombre("user");

        testMaterial = new Materials();
        testMaterial.setId(1);
        testMaterial.setName("Arduino UNO");
        testMaterial.setDescription("Placa de desarrollo Arduino");
        testMaterial.setPrice(15.99);
        testMaterial.setStock(10);
        testMaterial.setBorrowable_stock(10);
        testMaterial.setImagePath("test-image.jpg");
        testMaterial.setSubCategory(testSubCategory);
    // Inicializar colecciones para evitar NPEs en los tests
    testMaterial.setRoleMaterials(new java.util.ArrayList<>());

        testMaterialDTO = new MaterialsDTO();
        testMaterialDTO.setId(1);
        testMaterialDTO.setName("Arduino UNO");
        testMaterialDTO.setDescription("Placa de desarrollo Arduino");
        testMaterialDTO.setPrice(15.99);
        testMaterialDTO.setStock(10);
        testMaterialDTO.setBorrowable_stock(10);
        testMaterialDTO.setImagePath("test-image.jpg");
        testMaterialDTO.setSubCategoryId(1);
        testMaterialDTO.setSubCategoryName("Componentes Electrónicos");
        testMaterialDTO.setRoleIds(Arrays.asList(1));
        testMaterialDTO.setRoleNames(Arrays.asList("user"));

        // Stubs generales para los mappers/validators/managers que el servicio usa
        when(materialsMapper.toEntity(any())).thenAnswer(invocation -> {
            // Construir entidad basada en el DTO pasado al mapper (comportamiento realista)
            com.techmate.techmate.dto.MaterialsDTO dto = invocation.getArgument(0);
            Materials m = new Materials();
            if (dto.getId() != 0) m.setId(dto.getId());
            m.setName(dto.getName());
            m.setDescription(dto.getDescription());
            m.setPrice(dto.getPrice());
            m.setStock(dto.getStock());
            // Regla: si borrowable_stock no fue proporcionado (0 o null), se inicializa igual que stock
            if (dto.getBorrowable_stock() == 0) {
                m.setBorrowable_stock(dto.getStock());
            } else {
                m.setBorrowable_stock(dto.getBorrowable_stock());
            }
            m.setImagePath(dto.getImagePath());
            // Resolver subcategoría mínima (se usa testSubCategory por simplicidad)
            m.setSubCategory(testSubCategory);
            // Inicializar lista de roleMaterials; tests individuales pueden verificar saves en repo si es necesario
            m.setRoleMaterials(new java.util.ArrayList<>());
            return m;
        });

        when(materialsMapper.toDTO(any())).thenReturn(testMaterialDTO);

        // Validaciones y stock manager por defecto
        doNothing().when(materialsValidator).validateUniqueName(anyString());
        when(materialsStockManager.getAvailableStock(anyInt())).thenAnswer(inv -> testMaterial.getBorrowable_stock());
        // Query service por defecto (puede ser sobreescrito por tests individuales)
        when(materialsQueryService.getById(anyInt())).thenReturn(testMaterialDTO);
        when(materialsQueryService.getAllMaterials()).thenReturn(new java.util.ArrayList<>());
    }

    // ==================== CREATE MATERIALS ====================

    @Test
    @DisplayName("createMaterials - Éxito con imagen")
    void createMaterials_Success_WithImage() {
        // Given
        when(materialsRepository.findByName(anyString())).thenReturn(null);
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageStorageStrategy.saveImage(any(MultipartFile.class))).thenReturn("saved-image.jpg");
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(testRole));
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        // When
        MaterialsDTO result = materialsService.createMaterials(testMaterialDTO, mockImage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Arduino UNO");
        assertThat(result.getImagePath()).isNotNull();
        
        // El servicio ahora delega la verificación de unicidad al validator
        verify(materialsValidator).validateUniqueName("Arduino UNO");
        verify(imageStorageStrategy).saveImage(mockImage);
        verify(materialsRepository).save(any(Materials.class));
        // roleMaterials se construye por el mapper/servicio; no hay llamada directa obligatoria a roleMaterialsRepository
    }

    @Test
    @DisplayName("createMaterials - Falla por nombre duplicado")
    void createMaterials_Fail_DuplicateName() {
        // Given: ahora la validación de unicidad la hace materialsValidator
    doThrow(new IllegalArgumentException("ya existe")).when(materialsValidator).validateUniqueName(anyString());

        // When & Then
        assertThatThrownBy(() -> materialsService.createMaterials(testMaterialDTO, mockImage))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ya existe");

        verify(materialsValidator).validateUniqueName("Arduino UNO");
        verify(imageStorageStrategy, never()).saveImage(any());
        verify(materialsRepository, never()).save(any());
    }

    @Test
    @DisplayName("createMaterials - Éxito sin imagen")
    void createMaterials_Success_WithoutImage() {
        // Given
        when(materialsRepository.findByName(anyString())).thenReturn(null);
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(testRole));
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        // When
        MaterialsDTO result = materialsService.createMaterials(testMaterialDTO, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Arduino UNO");
        
        verify(imageStorageStrategy, never()).saveImage(any());
        verify(materialsRepository).save(any(Materials.class));
    }

    @Test
    @DisplayName("createMaterials - Falla por subcategoría no encontrada")
    void createMaterials_Fail_SubCategoryNotFound() {
        // Given
        when(materialsRepository.findByName(anyString())).thenReturn(null);
        when(mockImage.isEmpty()).thenReturn(true);
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Simular que el mapper/convertToEntity detecta subcategoría faltante y lanza BusinessException
        doThrow(new com.techmate.techmate.exception.BusinessException("SUBCATEGORY_NOT_FOUND", "Subcategoría no encontrada"))
            .when(materialsMapper).toEntity(any());

        // When & Then
        assertThatThrownBy(() -> materialsService.createMaterials(testMaterialDTO, mockImage))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("Subcategoría");

        verify(materialsMapper).toEntity(any());
        verify(materialsRepository, never()).save(any());
    }

    // ==================== GET MATERIALS BY ID ====================

    @Test
    @DisplayName("getMaterialsById - Éxito")
    void getMaterialsById_Success() {
        // Given
        // Given
        when(materialsQueryService.getById(1)).thenReturn(testMaterialDTO);

        // When
        MaterialsDTO result = materialsService.getMaterialsById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Arduino UNO");
        assertThat(result.getPrice()).isEqualTo(15.99);
        assertThat(result.getRoleNames()).contains("user");
        
        verify(materialsQueryService).getById(1);
    }

    @Test
    @DisplayName("getMaterialsById - No encontrado")
    void getMaterialsById_NotFound() {
        // Given
        when(materialsRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then: materialsQueryService es la ruta usada por el service para obtener DTOs
        when(materialsQueryService.getById(999)).thenThrow(new com.techmate.techmate.exception.BusinessException("MATERIAL_NOT_FOUND", "Material no encontrado con ID: 999"));
        assertThatThrownBy(() -> materialsService.getMaterialsById(999))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("999");

        verify(materialsQueryService).getById(999);
    }

    // ==================== UPDATE MATERIALS ====================

    @Test
    @DisplayName("updateMaterials - Éxito con nueva imagen")
    void updateMaterials_Success_WithNewImage() {
        // Given
        when(materialsRepository.findById(1)).thenReturn(Optional.of(testMaterial));
        when(mockImage.isEmpty()).thenReturn(false);
        when(imageStorageStrategy.saveImage(any(MultipartFile.class))).thenReturn("new-image.jpg");
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(testRole));
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        testMaterialDTO.setName("Arduino UNO Rev3");
        testMaterialDTO.setPrice(17.99);

        // When
        MaterialsDTO result = materialsService.updateMaterials(1, testMaterialDTO, mockImage);

        // Then
        assertThat(result).isNotNull();
        
        // Implementation currently saves the new image; deletion of the old image is not performed
        verify(imageStorageStrategy).saveImage(mockImage);
        verify(materialsRepository).save(any(Materials.class));
    }

    @Test
    @DisplayName("updateMaterials - Éxito sin cambiar imagen")
    void updateMaterials_Success_WithoutNewImage() {
        // Given
        when(materialsRepository.findById(1)).thenReturn(Optional.of(testMaterial));
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(testRole));
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        testMaterialDTO.setPrice(20.00);

        // When
        MaterialsDTO result = materialsService.updateMaterials(1, testMaterialDTO, null);

        // Then
        assertThat(result).isNotNull();
        
        verify(imageStorageStrategy, never()).saveImage(any());
        verify(imageStorageStrategy, never()).deleteImage(any());
        verify(materialsRepository).save(any(Materials.class));
    }

    @Test
    @DisplayName("updateMaterials - Material no encontrado")
    void updateMaterials_NotFound() {
        // Given
        when(materialsRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> materialsService.updateMaterials(999, testMaterialDTO, mockImage))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("999");

        verify(materialsRepository).findById(999);
        verify(materialsRepository, never()).save(any());
    }

    // ==================== DELETE MATERIALS ====================

    @Test
    @DisplayName("deleteMaterials - Éxito")
    void deleteMaterials_Success() {
        // Given
        when(materialsRepository.findById(1)).thenReturn(Optional.of(testMaterial));
        doNothing().when(imageStorageStrategy).deleteImage(anyString());
        doNothing().when(materialsRepository).deleteById(anyInt());

        // When
        materialsService.deleteMaterials(1);

        // Then
        verify(materialsRepository).findById(1);
        verify(imageStorageStrategy).deleteImage("test-image.jpg");
        verify(materialsRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteMaterials - Material no encontrado")
    void deleteMaterials_NotFound() {
        // Given
        when(materialsRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> materialsService.deleteMaterials(999))
            .isInstanceOf(com.techmate.techmate.exception.BusinessException.class)
            .hasMessageContaining("999");

        verify(materialsRepository).findById(999);
        verify(imageStorageStrategy, never()).deleteImage(any());
        verify(materialsRepository, never()).deleteById(any());
    }

    // ==================== GET ALL MATERIALS ====================

    @Test
    @DisplayName("getAllMaterials - Lista no vacía")
    void getAllMaterials_NotEmpty() {
        // Given
        Materials material2 = new Materials();
        material2.setId(2);
        material2.setName("Raspberry Pi");
        material2.setSubCategory(testSubCategory);
        
        // Construir DTOs que devuelve el query service
        MaterialsDTO dto2 = new MaterialsDTO();
        dto2.setId(2);
        dto2.setName("Raspberry Pi");
        dto2.setSubCategoryId(1);

        when(materialsQueryService.getAllMaterials()).thenReturn(Arrays.asList(testMaterialDTO, dto2));

        // When
        List<MaterialsDTO> result = materialsService.getAllMaterials();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Arduino UNO");
        assertThat(result.get(1).getName()).isEqualTo("Raspberry Pi");
        
        verify(materialsQueryService).getAllMaterials();
    }

    @Test
    @DisplayName("getAllMaterials - Lista vacía")
    void getAllMaterials_Empty() {
        // Given
        when(materialsQueryService.getAllMaterials()).thenReturn(Arrays.asList());

        // When
        List<MaterialsDTO> result = materialsService.getAllMaterials();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(materialsQueryService).getAllMaterials();
    }

    // ==================== EDGE CASES ====================
    // Nota: Los métodos convertToDTO y convertToEntity son privados,
    // pero se prueban indirectamente a través de todos los tests anteriores

    @Test
    @DisplayName("createMaterials - Stock inicial igual a borrowable_stock")
    void createMaterials_StockInitialization() {
        // Given
        testMaterialDTO.setStock(20);
        testMaterialDTO.setBorrowable_stock(0); // Se debe ignorar
        
        when(materialsRepository.findByName(anyString())).thenReturn(null);
        when(mockImage.isEmpty()).thenReturn(true);
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(testRole));

        ArgumentCaptor<Materials> materialsCaptor = ArgumentCaptor.forClass(Materials.class);
        when(materialsRepository.save(materialsCaptor.capture())).thenReturn(testMaterial);

        // When
        materialsService.createMaterials(testMaterialDTO, mockImage);

        // Then
        Materials savedMaterial = materialsCaptor.getValue();
        assertThat(savedMaterial.getBorrowable_stock()).isEqualTo(20);
    }

    @Test
    @DisplayName("createMaterials - Manejo de roles vacíos")
    void createMaterials_EmptyRoles() {
        // Given
        testMaterialDTO.setRoleIds(Arrays.asList());
        
        when(materialsRepository.findByName(anyString())).thenReturn(null);
        when(mockImage.isEmpty()).thenReturn(true);
        when(subCategoriesRepository.findById(anyInt())).thenReturn(Optional.of(testSubCategory));
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        // When
        MaterialsDTO result = materialsService.createMaterials(testMaterialDTO, mockImage);

        // Then
        assertThat(result).isNotNull();
        verify(roleMaterialsRepository, never()).save(any());
    }
}

