package com.techmate.techmate.hexagonal.infrastructure.service.materials.manager;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Role;
import com.techmate.techmate.hexagonal.domain.entity.RoleMaterials;
import com.techmate.techmate.hexagonal.domain.entity.SubCategories;
// import com.techmate.techmate.hexagonal.infrastructure.event.MaterialLowStockEvent; // TODO: Event system
import com.techmate.techmate.hexagonal.infrastructure.exception.BusinessException;
import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import com.techmate.techmate.hexagonal.domain.repository.RoleRepository;
import com.techmate.techmate.hexagonal.domain.repository.SubCategoriesRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.materials.mapper.MaterialsMapper;
import com.techmate.techmate.hexagonal.infrastructure.service.materials.validator.MaterialsValidator;
import com.techmate.techmate.hexagonal.infrastructure.validation.ImageValidationStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Gestor de operaciones CRUD y lógica de negocio para Materials (SRP).
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de CREATE, UPDATE, DELETE y eventos de Materials
 * - OCP: Extensible para nuevas operaciones sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para gestión de ciclo de vida
 * - DIP: Depende de abstracciones (Repository, Mapper, Validator) no de implementaciones
 * 
 * RESPONSABILIDADES:
 * - Crear nuevos materiales con validación e imagen
 * - Actualizar materiales existentes (datos, imagen, relaciones)
 * - Eliminar materiales de la base de datos
 * - Gestionar relaciones (subcategoría, roles)
 * - Procesar y almacenar imágenes
 * - Publicar eventos de bajo stock
 * 
 * PATRÓN:
 * - Delega validaciones a MaterialsValidator
 * - Delega conversión Entity ↔ DTO a MaterialsMapper
 * - Delega almacenamiento de imágenes a ImageStorageStrategy
 * - Publica eventos de dominio (MaterialLowStockEvent) para alertas asincrónicas
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Component
public class MaterialsManager {

    private final MaterialsRepository materialsRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final RoleRepository roleRepository;
    private final ImageValidationStrategy imageValidationStrategy;
    private final ImageStorageStrategy imageStorageStrategy;
    private final MaterialsMapper materialsMapper;
    private final MaterialsValidator materialsValidator;
    private final MaterialsStockManager materialsStockManager;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Umbral de stock bajo para eventos de alerta.
     */
    private static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * Constructor injection para cumplir con DIP.
     */
    public MaterialsManager(
            MaterialsRepository materialsRepository,
            SubCategoriesRepository subCategoriesRepository,
            RoleRepository roleRepository,
            ImageValidationStrategy imageValidationStrategy,
            ImageStorageStrategy imageStorageStrategy,
            MaterialsMapper materialsMapper,
            MaterialsValidator materialsValidator,
            MaterialsStockManager materialsStockManager,
            ApplicationEventPublisher eventPublisher) {
        this.materialsRepository = materialsRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.roleRepository = roleRepository;
        this.imageValidationStrategy = imageValidationStrategy;
        this.imageStorageStrategy = imageStorageStrategy;
        this.materialsMapper = materialsMapper;
        this.materialsValidator = materialsValidator;
        this.materialsStockManager = materialsStockManager;
        this.eventPublisher = eventPublisher;
    }

    // ==================== OPERACIÓN: CREATE ====================

    /**
     * Crea un nuevo material en el sistema.
     * 
     * FLUJO:
     * 1. Validar que no exista otro material con el mismo nombre
     * 2. Validar y guardar imagen si se proporciona
     * 3. Convertir DTO a entidad
     * 4. Persistir en base de datos
     * 5. Publicar eventos si el stock es bajo
     * 6. Retornar DTO del material creado
     * 
     * TRANSACCIONALIDAD:
     * - Si alguna operación falla, todo se revierte (atomicidad)
     * 
     * @param materialsDTO Datos del material a crear
     * @param image        Archivo de imagen (opcional)
     * @return MaterialsDTO del material creado (con ID asignado)
     * @throws IllegalArgumentException si ya existe material con ese nombre
     */
    @Transactional
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
        log.info("Iniciando creación de material: {}", materialsDTO.getName());

        // 1. Validar nombre único
        materialsValidator.validateUniqueName(materialsDTO.getName());

        // 2. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            processMaterialImage(materialsDTO, image);
        }

        // 3. Convertir y persistir
        Materials materials = materialsMapper.toEntity(materialsDTO);
        Materials savedMaterial = materialsRepository.save(materials);

        log.info("Material creado exitosamente con ID: {}", savedMaterial.getId());

        // 4. Publicar eventos de bajo stock
        checkAndPublishLowStockEvent(savedMaterial);

        // 5. Retornar DTO
        return materialsMapper.toDTO(savedMaterial);
    }

    // ==================== OPERACIÓN: UPDATE ====================

    /**
     * Actualiza un material existente.
     * 
     * FLUJO:
     * 1. Encontrar material por ID
     * 2. Actualizar atributos básicos
     * 3. Resolver y actualizar subcategoría
     * 4. Procesar y actualizar imagen si se proporciona
     * 5. Actualizar roles asociados
     * 6. Persistir cambios
     * 7. Publicar eventos si aplica
     * 8. Retornar DTO actualizado
     * 
     * @param materialsId ID del material a actualizar
     * @param materialsDTO Datos con los nuevos valores
     * @param image        Nuevo archivo de imagen (opcional)
     * @return MaterialsDTO del material actualizado
     * @throws BusinessException si el material no existe
     */
    @Transactional
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        log.info("Iniciando actualización de material ID: {}", materialsId);

        // 1. Encontrar Materials existente
        Materials existingMaterial = findMaterialById(materialsId);

        // 2. Actualizar atributos básicos
        updateMaterialAttributes(existingMaterial, materialsDTO);

        // 3. Resolver subcategoría
        updateMaterialSubCategory(existingMaterial, materialsDTO.getSubCategoryId());

        // 4. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            updateMaterialImage(existingMaterial, image);
        }

        // 5. Actualizar roles
        updateMaterialRoles(existingMaterial, materialsDTO.getRoleIds());

        // 6. Persistir
        Materials updatedMaterial = materialsRepository.save(existingMaterial);

        log.info("Material actualizado exitosamente ID: {}", materialsId);

        // 7. Publicar eventos
        checkAndPublishLowStockEvent(updatedMaterial);

        // 8. Retornar DTO
        return materialsMapper.toDTO(updatedMaterial);
    }

    // ==================== OPERACIÓN: DELETE ====================

    /**
     * Elimina un material del sistema.
     * 
     * FLUJO:
     * 1. Encontrar material por ID
     * 2. Eliminar imagen si existe (best-effort)
     * 3. Eliminar material de la base de datos
     * 
     * NOTA:
     * - Si la eliminación de imagen falla, continúa igual
     * - No bloquea la eliminación lógica del recurso
     * 
     * @param materialsId ID del material a eliminar
     * @throws BusinessException si el material no existe
     */
    @Transactional
    public void deleteMaterials(int materialsId) {
        log.info("Iniciando eliminación de material ID: {}", materialsId);

        // 1. Encontrar Materials existente
        Materials materials = findMaterialById(materialsId);

        // 2. Intentar eliminar imagen (best-effort)
        String imagePath = materials.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(imagePath);
                log.debug("Imagen eliminada para material ID: {}", materialsId);
            } catch (Exception ex) {
                log.warn("No se pudo eliminar imagen para material ID: {} - Continuando con eliminación lógica", 
                         materialsId, ex);
            }
        }

        // 3. Eliminar de base de datos
        materialsRepository.deleteById(materialsId);

        log.info("Material eliminado exitosamente ID: {}", materialsId);
    }

    // ==================== HELPERS ====================

    /**
     * Busca un material por ID.
     * 
     * @param materialsId ID del material
     * @return Materials entidad encontrada
     * @throws BusinessException si no existe
     */
    private Materials findMaterialById(int materialsId) {
        return materialsRepository.findById(materialsId)
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND",
                        "Material no encontrado con ID: " + materialsId));
    }

    /**
     * Actualiza atributos básicos del material.
     * 
     * @param material Entidad a actualizar
     * @param dto      Datos con nuevos valores
     */
    private void updateMaterialAttributes(Materials material, MaterialsDTO dto) {
        if (dto.getName() != null) {
            material.setName(dto.getName());
        }
        material.setDescription(dto.getDescription());
        material.setPrice(dto.getPrice());
        material.setStock(material.getStock()); // Mantener stock actual
    }

    /**
     * Actualiza la subcategoría del material.
     * 
     * @param material      Entidad a actualizar
     * @param subCategoryId ID de la nueva subcategoría
     */
    private void updateMaterialSubCategory(Materials material, Integer subCategoryId) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new BusinessException("SUBCATEGORY_NOT_FOUND",
                        "Subcategoría no encontrada con ID: " + subCategoryId));
        material.setSubCategory(subCategory);
    }

    /**
     * Procesa y almacena imagen de material.
     * 
     * @param dto   DTO a actualizar con ruta de imagen
     * @param image Archivo de imagen
     */
    private void processMaterialImage(MaterialsDTO dto, MultipartFile image) {
        imageValidationStrategy.validate(image);
        String savedImagePath = imageStorageStrategy.saveImage(image);
        dto.setImagePath(savedImagePath);
    }

    /**
     * Actualiza imagen del material existente.
     * 
     * @param material Entidad a actualizar
     * @param image    Nuevo archivo de imagen
     */
    private void updateMaterialImage(Materials material, MultipartFile image) {
        imageValidationStrategy.validate(image);
        String savedImagePath = imageStorageStrategy.saveImage(image);
        material.setImagePath(savedImagePath);
    }

    /**
     * Actualiza los roles asociados al material.
     * 
     * @param material Entidad a actualizar
     * @param roleIds  IDs de los nuevos roles
     */
    private void updateMaterialRoles(Materials material, List<Integer> roleIds) {
        List<RoleMaterials> updatedRoleMaterials = roleIds.stream()
                .map(roleId -> createRoleMaterialsAssociation(material, roleId))
                .collect(Collectors.toList());

        material.getRoleMaterials().clear();
        material.getRoleMaterials().addAll(updatedRoleMaterials);
    }

    /**
     * Crea una asociación role-material.
     * 
     * @param material Material asociado
     * @param roleId   ID del rol
     * @return RoleMaterials nueva asociación
     */
    private RoleMaterials createRoleMaterialsAssociation(Materials material, Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND",
                        "Rol no encontrado con ID: " + roleId));

        RoleMaterials roleMaterials = new RoleMaterials();
        roleMaterials.setRole(role);
        roleMaterials.setMaterials(material);
        return roleMaterials;
    }

    /**
     * Verifica si el stock está por debajo del umbral y publica evento si aplica.
     * 
     * PROPÓSITO:
     * - Detectar materiales con stock crítico
     * - Notificar al sistema mediante eventos de dominio
     * - Permitir reacciones asincrónicas (emails, alertas, compras automáticas)
     * 
     * @param material El material a verificar
     */
    private void checkAndPublishLowStockEvent(Materials material) {
        if (material.getStock() < LOW_STOCK_THRESHOLD) {
            log.warn("Stock bajo detectado para material ID: {} - Stock: {}", 
                     material.getId(), material.getStock());
            // TODO: Publish MaterialLowStockEvent when event system is implemented
            // MaterialLowStockEvent event = new MaterialLowStockEvent(material, LOW_STOCK_THRESHOLD);
            // eventPublisher.publishEvent(event);
        }
    }
}







