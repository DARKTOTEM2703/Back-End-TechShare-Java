package com.techmate.techmate.infrastructure.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.dto.SubCategoriesDTO;

public interface SubCategoriesService {
    

    SubCategoriesDTO createSubCategory (SubCategoriesDTO subCategoryDTO,  MultipartFile image);
    SubCategoriesDTO getSubCategoryById(int subCategoryID);
    SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO,  MultipartFile image);
    void deleteSubCategory(int subCategoryID);
    List<SubCategoriesDTO> getAllSubCategories();


    String getSubCategoryNameById(int subCategoryID);
}









