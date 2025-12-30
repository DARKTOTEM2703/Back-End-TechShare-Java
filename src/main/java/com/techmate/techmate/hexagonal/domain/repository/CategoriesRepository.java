package com.techmate.techmate.hexagonal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.hexagonal.domain.entity.Categories;

/**
 * La interfaz {@code CategoriesRepository} es un repositorio que proporciona
 * métodos para realizar operaciones de acceso a datos en la entidad {@code Categories}.
 * 
 * <p>Esta interfaz extiende {@code JpaRepository}, lo que permite el uso de 
 * operaciones CRUD y consultas personalizadas para la entidad.</p>
 * 
 * <p>Se anotada con {@code @Repository}, lo que permite que Spring la 
 * reconozca como un componente de acceso a datos.</p>
 */
@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {

    /**
 * Busca una categoría por su nombre.
 *
 * @param name El nombre de la categoría.
 * @return La categoría correspondiente al nombre proporcionado.
 */
Categories findByName(String name);


    /**
     * Busca el nombre de la categoría a partir de su identificador único.
     *
     * @param categoryId El identificador único de la categoría.
     * @return El nombre de la categoría correspondiente al {@code categoryId}.
     *         Si no se encuentra la categoría, el valor devuelto será {@code null}.
     */
    @Query("SELECT c.name FROM Categories c WHERE c.id = :categoryId")
    String findNameByCategoryId(@Param("categoryId") int categoryId);
}



