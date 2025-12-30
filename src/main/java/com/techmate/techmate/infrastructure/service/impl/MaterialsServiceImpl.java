package com.techmate.techmate.infrastructure.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.service.MaterialsService;
import com.techmate.techmate.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.domain.repository.MaterialsRepository;
import com.techmate.techmate.infrastructure.service.materials.manager.MaterialsManager;
import com.techmate.techmate.infrastructure.service.materials.query.MaterialsQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Implementación del servicio de materiales (Facade).
 * 
 * Esta clase actúa como un COORDINADOR (Facade Pattern).
 * No contiene lógica de negocio compleja, solo delega responsabilidades a:
 * 1. MaterialsQueryService    → Para lecturas y búsquedas
 * 2. MaterialsManager         → Para CRUD y lógica de negocio
 * 3. MaterialsRepository      → Para acceso directo a persistencia (paginación)
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo coordina operaciones, no contiene lógica de negocio
 * - OCP: Extensible mediante inyección de nuevos componentes
 * - LSP: Implementa correctamente la interfaz MaterialsService
 * - ISP: Delega a interfaces específicas (Manager, QueryService)
 * - DIP: Depende de abstracciones, no de implementaciones concretas
 * 
 * PATRÓN ARQUITECTÓNICO:
 * Controller → MaterialsServiceImpl (Facade)
 *           ├→ MaterialsQueryService (Consultas: GET, FIND, SEARCH)
 *           ├→ MaterialsManager (CRUD: CREATE, UPDATE, DELETE + lógica)
 *           └→ MaterialsRepository (Acceso a datos)
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialsServiceImpl implements MaterialsService {

    // ==================== DEPENDENCIAS INYECTADAS ====================

    /**
     * Servicio de consultas especializado para materiales.
     * Responsable de lecturas: findAll, findById, búsquedas, filtros.
     */
    private final MaterialsQueryService materialsQueryService;

    /**
     * Gestor de operaciones CRUD y lógica de negocio.
     * Responsable de escrituras: create, update, delete.
     */
    private final MaterialsManager materialsManager;

    /**
     * Repository directo para acceso a persistencia de bajo nivel.
     * Usado para operaciones específicas como paginación.
     */
    private final MaterialsRepository materialsRepository;


    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * 📋 Obtiene todos los materiales.
     * Delega a MaterialsQueryService.
     */
    @Override
    public java.util.List<MaterialsDTO> getAllMaterials() {
        return materialsQueryService.getAllMaterials();
    }

    /**
     * 🔍 Obtiene un material por su ID.
     * Delega a MaterialsQueryService.
     */
    @Override
    public MaterialsDTO getMaterialsById(int materialsId) {
        return materialsQueryService.getById(materialsId);
    }

    /**
     * 🔤 Obtiene el nombre de un material por su ID.
     * Delega a MaterialsRepository.
     */
    @Override
    public String getMaterialsNameById(int materialId) {
        return materialsRepository.findById(materialId)
                .map(com.techmate.techmate.domain.entity.Materials::getName)
                .orElse(null);
    }

    /**
     * 💰 Obtiene todos los materiales ordenados por precio.
     * Delega a MaterialsQueryService.
     */
    @Override
    public java.util.List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending) {
        return materialsQueryService.getAllMaterialsSortedByPrice(ascending);
    }

    /**
     * 📄 Obtiene todos los materiales con paginación.
     * Delega a MaterialsRepository (acceso directo necesario para paginación).
     */
    @Override
    public Page<MaterialsDTO> getAllMaterialsPaginated(Pageable pageable) {
        Page<com.techmate.techmate.domain.entity.Materials> materialsPage = materialsRepository.findAll(pageable);
        return materialsPage.map(materials -> {
            // Se usa MaterialsQueryService para consistencia en mapeo
            return materialsQueryService.getById(materials.getId());
        });
    }

    // ==================== OPERACIONES DE ESCRITURA ====================

    /**
     * ✨ Crea un nuevo material.
     * Delega a MaterialsManager.
     */
    @Override
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
        return materialsManager.createMaterials(materialsDTO, image);
    }

    /**
     * 🔄 Actualiza un material existente.
     * Delega a MaterialsManager.
     */
    @Override
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        return materialsManager.updateMaterials(materialsId, materialsDTO, image);
    }

    /**
     * 🗑️ Elimina un material.
     * Delega a MaterialsManager.
     */
    @Override
    public void deleteMaterials(int materialsId) {
        materialsManager.deleteMaterials(materialsId);
    }
}







