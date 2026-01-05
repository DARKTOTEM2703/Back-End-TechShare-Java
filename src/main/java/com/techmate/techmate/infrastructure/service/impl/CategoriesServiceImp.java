package com.techmate.techmate.infrastructure.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.dto.CategoriesDTO;
import com.techmate.techmate.infrastructure.persistence.repository.CategoriesRepository;
import com.techmate.techmate.infrastructure.service.CategoriesService;
import com.techmate.techmate.infrastructure.service.categories.manager.CategoriesManager;
import com.techmate.techmate.infrastructure.service.categories.query.CategoriesQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Implementación del servicio de categorías (Facade).
 * 
 * Esta clase actúa como un COORDINADOR (Facade Pattern).
 * No contiene lógica de negocio compleja, solo delega responsabilidades a:
 * 1. CategoriesQueryService → Para lecturas y búsquedas
 * 2. CategoriesManager      → Para CRUD y lógica de negocio
 * 3. CategoriesRepository   → Para acceso directo a persistencia
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo coordina operaciones, no contiene lógica de negocio
 * - OCP: Extensible mediante inyección de nuevos componentes
 * - LSP: Implementa correctamente la interfaz CategoriesService
 * - ISP: Delega a interfaces específicas (Manager, QueryService)
 * - DIP: Depende de abstracciones, no de implementaciones concretas
 * 
 * PATRÓN ARQUITECTÓNICO:
 * Controller → CategoriesServiceImp (Facade)
 *           ├→ CategoriesQueryService (Consultas: GET, FIND, SEARCH)
 *           ├→ CategoriesManager (CRUD: CREATE, UPDATE, DELETE + lógica)
 *           └→ CategoriesRepository (Acceso a datos)
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesServiceImp implements CategoriesService {

    /**
     * Servicio de consultas especializado para categorías.
     * Responsable de lecturas: findAll, findById, búsquedas.
     */
    private final CategoriesQueryService categoriesQueryService;

    /**
     * Gestor de operaciones CRUD y lógica de negocio.
     * Responsable de escrituras: create, update, delete.
     */
    private final CategoriesManager categoriesManager;

    /**
     * Repository directo para acceso a persistencia de bajo nivel.
     * Usado para operaciones específicas.
     */
    private final CategoriesRepository categoriesRepository;



    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * 📋 Obtiene todas las categorías.
     * Delega a CategoriesQueryService.
     */
    @Override
    public List<CategoriesDTO> getAllCategories() {
        return categoriesQueryService.getAll();
    }

    /**
     * 🔍 Obtiene una categoría por su ID.
     * Delega a CategoriesQueryService.
     */
    @Override
    public CategoriesDTO getCategoryById(int categoryID) {
        return categoriesQueryService.getById(categoryID);
    }

    /**
     * 🔤 Obtiene el nombre de una categoría por su ID.
     * Delega a CategoriesRepository.
     */
    @Override
    public String getCategoryNameById(int categoryId) {
        return categoriesRepository.findById(categoryId)
                .map(com.techmate.techmate.domain.entity.Categories::getName)
                .orElse(null);
    }

    // ==================== OPERACIONES DE ESCRITURA ====================

    /**
     * ✨ Crea una nueva categoría.
     * Delega a CategoriesManager.
     */
    @Override
    public CategoriesDTO createCategory(CategoriesDTO categoriesDTO, MultipartFile image) {
        return categoriesManager.createCategory(categoriesDTO, image);
    }

    /**
     * 🔄 Actualiza una categoría existente.
     * Delega a CategoriesManager.
     */
    @Override
    public CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoriesDTO, MultipartFile image) {
        return categoriesManager.updateCategory(categoryID, categoriesDTO, image);
    }

    /**
     * 🗑️ Elimina una categoría.
     * Delega a CategoriesManager.
     */
    @Override
    public void deleteCategory(int categoryID) {
        categoriesManager.deleteCategory(categoryID);
    }
}







