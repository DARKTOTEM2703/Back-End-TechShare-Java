package com.techmate.techmate.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.infrastructure.persistence.entity.SubCategories;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoriesRepository extends JpaRepository<SubCategories, Integer> {

    List<SubCategories> findByCategoryId(Integer categoryId);

    Optional<SubCategories> findByName(String name);

    boolean existsByNameAndCategoryId(String name, Integer categoryId);
}
