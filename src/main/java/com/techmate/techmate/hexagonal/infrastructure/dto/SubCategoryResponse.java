package com.techmate.techmate.hexagonal.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryResponse {
    private int subCategoriesId;
    private String name;
    private String imagePath;
    private int categoryId;
    private String categoryName;
}








