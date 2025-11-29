package com.techmate.techmate.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.service.MaterialsService;
import com.techmate.techmate.validation.ImageValidationStrategy;
import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.event.MaterialLowStockEvent;
import com.techmate.techmate.exception.BusinessException;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;
import com.techmate.techmate.repository.MaterialsRepository;

/**
 * Implementación del servicio de gestión de materiales.
 * 
 * RESPONSABILIDADES:
 * 1. Gestionar el ciclo de vida completo de los materiales (CRUD)
 * 2. Coordinar el almacenamiento de imágenes asociadas
 * 3. Mantener la relación entre materiales, roles y subcategorías
 * 4. Convertir entre entidades JPA (Materials) y DTOs (MaterialsDTO)
 * 5. Validar reglas de negocio (nombres únicos, stock coherente, etc.)
 * 
 * FLUJO TÍPICO:
 * Controller → MaterialsService → MaterialsRepository → Base de Datos
 * ↓
 * ImageStorageStrategy (para archivos)
 * ↓
 * Validation (reglas de negocio)
 * 
 * ARQUITECTURA:
 * - Patrón Service: separa lógica de negocio de controllers
 * - Patrón Strategy: delega almacenamiento de imágenes a implementaciones
 * intercambiables
 * - Patrón DTO: previene exposición directa de entidades JPA al cliente
 * 
 * @author TechMate Team
 * @version 1.0
 * @since 2025-10-02
 */
@Service
public class MaterialsServiceImpl implements MaterialsService {

    // ==================== DEPENDENCIAS INYECTADAS ====================

    /**
     * Repository para acceso a datos de materiales.
     * Provee métodos CRUD + consultas personalizadas.
     */
    private final MaterialsRepository materialsRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final RoleRepository roleRepository;
    private final ImageValidationStrategy imageValidationStrategy;
    private final ImageStorageStrategy imageStorageStrategy;
    private final com.techmate.techmate.service.materials.mapper.MaterialsMapper materialsMapper;
    private final com.techmate.techmate.service.materials.validator.MaterialsValidator materialsValidator;
    private final com.techmate.techmate.service.materials.manager.MaterialsStockManager materialsStockManager;
    private final com.techmate.techmate.service.materials.query.MaterialsQueryService materialsQueryService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Umbral de stock bajo (Low Stock Threshold).
     * Cuando el stock de un material cae por debajo de este valor,
     * se dispara un MaterialLowStockEvent para alertar al sistema.
     */
    private static final int LOW_STOCK_THRESHOLD = 10;

    public MaterialsServiceImpl(
            MaterialsRepository materialsRepository,
            SubCategoriesRepository subCategoriesRepository,
            RoleRepository roleRepository,
            ImageValidationStrategy imageValidationStrategy,
            ImageStorageStrategy imageStorageStrategy,
            com.techmate.techmate.service.materials.mapper.MaterialsMapper materialsMapper,
            com.techmate.techmate.service.materials.validator.MaterialsValidator materialsValidator,
            com.techmate.techmate.service.materials.manager.MaterialsStockManager materialsStockManager,
            com.techmate.techmate.service.materials.query.MaterialsQueryService materialsQueryService,
            ApplicationEventPublisher eventPublisher) {
        this.materialsRepository = materialsRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.roleRepository = roleRepository;
        this.imageValidationStrategy = imageValidationStrategy;
        this.imageStorageStrategy = imageStorageStrategy;
        this.materialsMapper = materialsMapper;
        this.materialsValidator = materialsValidator;
        this.materialsStockManager = materialsStockManager;
        this.materialsQueryService = materialsQueryService;
        this.eventPublisher = eventPublisher;
    }

    // NOTE: Cada dependencia inyectada tiene una responsabilidad clara (SRP):
    // - materialsRepository: acceso a persistencia (DAO)
    // - subCategoriesRepository / roleRepository: resolución de relaciones
    // - imageValidationStrategy / imageStorageStrategy: delegación a estrategias
    // para manejar imágenes (Strategy pattern)
    // - materialsMapper: conversión Entity ↔ DTO (evita lógica de mapping en el
    // service)
    // - materialsValidator: validaciones de negocio (únicas responsabilidades)
    // - materialsStockManager: operaciones relacionadas al stock (aisla
    // concurrencia y transacciones)
    // - materialsQueryService: consultas complejas / proyecciones (separación de
    // consultas)

    // ==================== MÉTODOS DE CONVERSIÓN (ENTITY ↔ DTO)
    // ====================

    /**
     * Convierte una entidad Materials (JPA) a MaterialsDTO (transferencia).
     * 
     * PROPÓSITO:
     * - Preparar datos para enviar al frontend (JSON)
     * - Evitar exponer entidades JPA directamente (previene lazy loading issues)
     * - Aplanar relaciones complejas (ej: lista de RoleMaterials → lista de IDs)
     * 
     * TRANSFORMACIONES:
     * - Materials.subCategory (objeto) → subCategoryId + subCategoryName
     * - Materials.roleMaterials (List<RoleMaterials>) → roleIds[] + roleNames[]
     * 
     * @param materials Entidad JPA a convertir
     * @return MaterialsDTO listo para serializar a JSON
     */

    /**
     * Convierte un MaterialsDTO (desde el frontend) a entidad Materials (JPA).
     * 
     * PROPÓSITO:
     * - Preparar datos para persistir en la base de datos
     * - Resolver relaciones (cargar SubCategories y Roles desde BD)
     * - Inicializar colecciones y relaciones bidireccionales
     * 
     * VALIDACIONES:
     * - Verifica que la subcategoría existe (lanza RuntimeException si no)
     * - Verifica que todos los roles existen
     * 
     * REGLA DE NEGOCIO:
     * - borrowable_stock se inicializa igual que stock (todo disponible para
     * préstamo)
     * - Si stock = 0, ambos se setean a 0
     * 
     * @param materialsDTO DTO recibido del frontend
     * @return Materials entidad JPA lista para save()
     * @throws RuntimeException si subcategoría o algún rol no existe
     */
    private Materials convertToEntity(MaterialsDTO materialsDTO) {
        return materialsMapper.toEntity(materialsDTO);
    }

    // ==================== OPERACIONES CRUD ====================

    /**
     * Crea un nuevo material en el sistema.
     * 
     * FLUJO:
     * 1. Validar que no exista otro material con el mismo nombre (constraint de
     * negocio)
     * 2. Validar y guardar imagen si se proporciona
     * 3. Convertir DTO a entidad
     * 4. Persistir en base de datos
     * 5. Retornar DTO del material creado
     * 
     * VALIDACIONES:
     * - Nombre único (no puede haber dos materiales con igual nombre)
     * - Extensión de imagen válida (.jpg, .png, .gif, etc.)
     * - Subcategoría existe
     * - Todos los roles existen
     * 
     * TRANSACCIONALIDAD:
     * - Si alguna operación falla, todo se revierte (atomicidad)
     * - La imagen se guarda ANTES de la BD (para evitar referencias rotas)
     * 
     * @param materialsDTO Datos del material a crear (desde frontend)
     * @param image        Archivo de imagen (opcional, puede ser null)
     * @return MaterialsDTO del material creado (con ID asignado)
     * @throws IllegalArgumentException si ya existe material con ese nombre
     * @throws RuntimeException         si subcategoría o roles no existen, o imagen
     *                                  inválida
     */
    @Override
    @Transactional
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
        // 1. Validar nombre (SRP: Validación)
        validateMaterialName(materialsDTO.getName());

        // 2. Procesar imagen (SRP: Gestión de imágenes)
        if (image != null && !image.isEmpty()) {
            processMaterialImage(materialsDTO, image);
        }

        // 3. Persistir (SRP: Persistencia)
        Materials savedMaterial = persistMaterial(materialsDTO);

        // 4. Retornar DTO (SRP: Conversión)
        return materialsMapper.toDTO(savedMaterial);
    }

    // ==================== HELPERS: createMaterials() ====================

    /**
     * Valida que el nombre del material sea único en el sistema.
     * 
     * @param name Nombre del material a validar
     * @throws IllegalArgumentException si ya existe un material con ese nombre
     */
    private void validateMaterialName(String name) {
        materialsValidator.validateUniqueName(name);
    }

    /**
     * Procesa y almacena la imagen del material.
     * 
     * @param dto   DTO del material (se actualiza con ruta de imagen guardada)
     * @param image Archivo de imagen a procesar
     */
    private void processMaterialImage(MaterialsDTO dto, MultipartFile image) {
        // Validar imagen (extensión, tamaño, etc.)
        imageValidationStrategy.validate(image);

        // Guardar imagen en storage (filesystem, S3, etc.)
        String savedImagePath = imageStorageStrategy.saveImage(image);

        // Actualizar DTO con la ruta guardada
        dto.setImagePath(savedImagePath);
    }

    /**
     * Convierte el DTO a entidad y persiste en la base de datos.
     * 
     * @param materialsDTO DTO del material a persistir
     * @return Materials entidad guardada (con ID asignado)
     */
    private Materials persistMaterial(MaterialsDTO materialsDTO) {
        Materials materials = convertToEntity(materialsDTO);
        return materialsRepository.save(materials);
    }

    // ==================== HELPERS: updateMaterials() ====================

    /**
     * Busca un material por su ID.
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
     * Actualiza los atributos básicos del material.
     * 
     * @param material Entidad a actualizar
     * @param dto      Datos con los nuevos valores
     */
    private void updateMaterialAttributes(Materials material, MaterialsDTO dto) {
        if (dto.getName() != null) {
            material.setName(dto.getName());
        }
        material.setDescription(dto.getDescription());
        material.setPrice(dto.getPrice());
        material.setStock(material.getStock()); // Mantener stock actual

        // Resolver stock disponible desde el manager
        int available = materialsStockManager.getAvailableStock(material.getId());
        material.setBorrowable_stock(available);
    }

    /**
     * Actualiza la subcategoría del material.
     * 
     * @param material      Entidad a actualizar
     * @param subCategoryId ID de la nueva subcategoría
     * @throws BusinessException si la subcategoría no existe
     */
    private void updateMaterialSubCategory(Materials material, Integer subCategoryId) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new BusinessException("SUBCATEGORY_NOT_FOUND",
                        "Subcategoría no encontrada con ID: " + subCategoryId));
        material.setSubCategory(subCategory);
    }

    /**
     * Actualiza la imagen del material.
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
     * @throws BusinessException si el rol no existe
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
     * Persiste el material y publica eventos de bajo stock si es necesario.
     * 
     * @param material Material a persistir
     * @return Materials material guardado
     */
    private Materials persistAndPublishEvents(Materials material) {
        Materials updated = materialsRepository.save(material);
        checkAndPublishLowStockEvent(updated);
        return updated;
    }

    @Override
    @Transactional
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        // 1. Encontrar material (SRP: Obtención)
        Materials existingMaterial = findMaterialById(materialsId);

        // 2. Actualizar datos básicos (SRP: Actualización de atributos)
        updateMaterialAttributes(existingMaterial, materialsDTO);

        // 3. Resolver subcategoría (SRP: Resolución de relaciones)
        updateMaterialSubCategory(existingMaterial, materialsDTO.getSubCategoryId());

        // 4. Procesar imagen (SRP: Gestión de imágenes)
        if (image != null && !image.isEmpty()) {
            updateMaterialImage(existingMaterial, image);
        }

        // 5. Actualizar roles (SRP: Gestión de relaciones many-to-many)
        updateMaterialRoles(existingMaterial, materialsDTO.getRoleIds());

        // 6. Persistir y publicar eventos (SRP: Persistencia + eventos)
        Materials updatedMaterial = persistAndPublishEvents(existingMaterial);

        // 7. Retornar DTO
        return materialsMapper.toDTO(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteMaterials(int materialsId) {
        // Buscar el material por ID
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND",
                        "Material no encontrado con ID: " + materialsId));

        // Si se encuentra el material, eliminar la imagen si existe.
        // Nota: la eliminación de la imagen es una operación side-effect externa y
        // no participa en la transacción de la BD; si falla la eliminación, se
        // captura y se delega la política (retry/alert) a la estrategia de storage.
        String imagePath = materials.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
            } catch (Exception ex) {
                // No impedimos la eliminación lógica del recurso en BD; registrar/alertar.
                // Aquí preferimos continuar y eliminar el registro en BD para no dejar
                // basura referencial en la aplicación.
            }
        }

        // Eliminar el material de la base de datos
        materialsRepository.deleteById(materialsId);
    }

    @Override
    public List<MaterialsDTO> getAllMaterials() {
        return materialsQueryService.getAllMaterials();
    }

    /**
     * Obtiene todos los materiales con paginación y ordenamiento.
     * 
     * @param pageable Configuración de paginación (página, tamaño, ordenamiento)
     * @return Page de MaterialsDTO con metadatos de paginación
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MaterialsDTO> getAllMaterialsPaginated(Pageable pageable) {
        Page<Materials> materialsPage = materialsRepository.findAll(pageable);
        return materialsPage.map(materialsMapper::toDTO);
    }

    @Override
    public MaterialsDTO getMaterialsById(int materialsId) {
        return materialsQueryService.getById(materialsId);
    }

    @Override
    public String getMaterialsNameById(int materialId) {
        // Busca el material por su ID en el repositorio y obtiene el nombre si se
        // encuentra
        return materialsRepository.findById(materialId)
                .map(Materials::getName) // Obtiene el nombre del material si se encuentra
                .orElse(null); // Devuelve null si no se encuentra el material
    }

    @Override
    public List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending) {
        return materialsQueryService.getAllMaterialsSortedByPrice(ascending);
    }

    // ==================== DOMAIN EVENTS ====================

    /**
     * Verifica si el stock de un material está por debajo del umbral
     * y publica un MaterialLowStockEvent si es necesario.
     * 
     * PROPÓSITO:
     * - Detectar materiales con stock crítico
     * - Notificar al sistema mediante eventos de dominio
     * - Permitir reacciones asíncronas (emails, alertas, compras automáticas)
     * 
     * LÓGICA:
     * - Si stock < LOW_STOCK_THRESHOLD (10 unidades por defecto)
     * - Publicar evento MaterialLowStockEvent
     * - Los listeners (MaterialEventListener) reaccionarán de forma asíncrona
     * 
     * CASOS DE USO:
     * 1. Después de crear un material con stock inicial bajo
     * 2. Después de actualizar un material y reducir su stock
     * 3. Después de un préstamo que reduce el stock disponible
     * 
     * @param material El material a verificar
     */
    private void checkAndPublishLowStockEvent(Materials material) {
        if (material.getStock() < LOW_STOCK_THRESHOLD) {
            MaterialLowStockEvent event = new MaterialLowStockEvent(
                    material,
                    LOW_STOCK_THRESHOLD);
            eventPublisher.publishEvent(event);
        }
    }

}
