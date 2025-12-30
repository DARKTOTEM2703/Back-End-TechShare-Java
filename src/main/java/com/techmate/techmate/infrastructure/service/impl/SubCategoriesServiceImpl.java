package com.techmate.techmate.infrastructure.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.dto.SubCategoriesDTO;
import com.techmate.techmate.domain.repository.SubCategoriesRepository;
import com.techmate.techmate.infrastructure.service.SubCategoriesService;
import com.techmate.techmate.infrastructure.service.subcategories.manager.SubCategoriesManager;
import com.techmate.techmate.infrastructure.service.subcategories.query.SubCategoriesQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Implementación del servicio de subcategorías (Facade).
 * 
 * Esta clase actúa como un COORDINADOR (Facade Pattern).
 * No contiene lógica de negocio compleja, solo delega responsabilidades a:
 * 1. SubCategoriesQueryService → Para lecturas y búsquedas
 * 2. SubCategoriesManager      → Para CRUD y lógica de negocio
 * 3. SubCategoriesRepository   → Para acceso directo a persistencia
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo coordina operaciones, no contiene lógica de negocio
 * - OCP: Extensible mediante inyección de nuevos componentes
 * - LSP: Implementa correctamente la interfaz SubCategoriesService
 * - ISP: Delega a interfaces específicas (Manager, QueryService)
 * - DIP: Depende de abstracciones, no de implementaciones concretas
 * 
 * PATRÓN ARQUITECTÓNICO:
 * Controller → SubCategoriesServiceImpl (Facade)
 *           ├→ SubCategoriesQueryService (Consultas: GET, FIND, SEARCH)
 *           ├→ SubCategoriesManager (CRUD: CREATE, UPDATE, DELETE + lógica)
 *           └→ SubCategoriesRepository (Acceso a datos)
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubCategoriesServiceImpl implements SubCategoriesService {

    /**
     * Servicio de consultas especializado para subcategorías.
     * Responsable de lecturas: findAll, findById, búsquedas.
     */
    private final SubCategoriesQueryService subCategoriesQueryService;

    /**
     * Gestor de operaciones CRUD y lógica de negocio.
     * Responsable de escrituras: create, update, delete.
     */
    private final SubCategoriesManager subCategoriesManager;

    /**
     * Repository directo para acceso a persistencia de bajo nivel.
     */
    private final SubCategoriesRepository subCategoriesRepository;



    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * 📋 Obtiene todas las subcategorías.
     * Delega a SubCategoriesQueryService.
     */
    @Override
    public List<SubCategoriesDTO> getAllSubCategories() {
        return subCategoriesQueryService.getAll();
    }

    /**
     * 🔍 Obtiene una subcategoría por su ID.
     * Delega a SubCategoriesQueryService.
     */
    @Override
    public SubCategoriesDTO getSubCategoryById(int subCategoryID) {
        return subCategoriesQueryService.getById(subCategoryID);
    }

    /**
     * 🔤 Obtiene el nombre de una subcategoría por su ID.
     * Delega a SubCategoriesQueryService.
     */
    @Override
    public String getSubCategoryNameById(int subCategoryID) {
        return subCategoriesQueryService.getNameById(subCategoryID);
    }

    // ==================== OPERACIONES DE ESCRITURA ====================

    /**
     * ✨ Crea una nueva subcategoría.
     * Delega a SubCategoriesManager.
     */
    @Override
    public SubCategoriesDTO createSubCategory(SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        return subCategoriesManager.createSubCategory(subCategoryDTO, image);
    }

    /**
     * 🔄 Actualiza una subcategoría existente.
     * Delega a SubCategoriesManager.
     */
    @Override
    public SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        return subCategoriesManager.updateSubCategory(subCategoryID, subCategoryDTO, image);
    }

    /**
     * 🗑️ Elimina una subcategoría.
     * Delega a SubCategoriesManager.
     */
    @Override
    public void deleteSubCategory(int subCategoryID) {
        subCategoriesManager.deleteSubCategory(subCategoryID);
    }
}







