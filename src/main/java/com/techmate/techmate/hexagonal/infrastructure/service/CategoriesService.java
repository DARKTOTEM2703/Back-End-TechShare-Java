package com.techmate.techmate.hexagonal.infrastructure.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.hexagonal.infrastructure.dto.CategoriesDTO;

/**
 * La interfaz {@code CategoriesService} define los métodos para manejar
 * operaciones relacionadas con las categorías en el sistema.
 * 
 * <p>Proporciona operaciones CRUD básicas y métodos para la gestión
 * de categorías, utilizando objetos de transferencia de datos ({@code CategoriesDTO}).</p>
 */
public interface CategoriesService {

    /**
     * Crea una nueva categoría.
     * 
     * @param categoryDTO El objeto que contiene la información de la categoría a crear.
     * @return El objeto {@code CategoriesDTO} creado.
     */
    CategoriesDTO createCategory(CategoriesDTO categoryDTO, MultipartFile image);

    /**
     * Obtiene una categoría por su identificador único.
     * 
     * @param categoryID El identificador único de la categoría.
     * @return El objeto {@code CategoriesDTO} correspondiente, o {@code null} si no se encuentra.
     */
    CategoriesDTO getCategoryById(int categoryID);

    /**
     * Actualiza una categoría existente.
     * 
     * @param categoryID El identificador único de la categoría a actualizar.
     * @param categoryDTO El objeto que contiene la nueva información de la categoría.
     * @return El objeto {@code CategoriesDTO} actualizado.
     */
    CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoryDTO, MultipartFile image);

    /**
     * Elimina una categoría por su identificador único.
     * 
     * @param categoryID El identificador único de la categoría a eliminar.
     */
    void deleteCategory(int categoryID);
    
    /**
     * Obtiene una lista de todas las categorías.
     * 
     * @return Una lista de objetos {@code CategoriesDTO} que representan todas las categorías.
     */
    List<CategoriesDTO> getAllCategories();

    /**
     * Obtiene el nombre de una categoría por su identificador único.
     * 
     * @param categoryId El identificador único de la categoría.
     * @return El nombre de la categoría correspondiente al {@code categoryId}, o {@code null} si no se encuentra.
     */
    String getCategoryNameById(int categoryId);
}




