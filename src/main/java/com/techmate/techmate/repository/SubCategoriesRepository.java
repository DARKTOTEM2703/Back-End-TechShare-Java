package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.SubCategories;

@Repository
public interface SubCategoriesRepository extends JpaRepository<SubCategories, Integer>{
    /*@Query("SELECT s.name FROM SubCategories s WHERE s.id = ?1")
    String findNameSubCategoryId(int SubCategoryId);¨*/


    SubCategories findByName(String name);
}



