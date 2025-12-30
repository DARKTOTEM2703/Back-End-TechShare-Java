package com.techmate.techmate.infrastructure.dto;

/**
 * DTO usado para respuestas de categoría que se envían al cliente.
 * Tiene la estructura completa que queremos exponer públicamente.
 */
public class CategoryResponse {

    private int categoryId;
    private String name;
    private String imagePath;

    public CategoryResponse() {
    }

    public CategoryResponse(int categoryId, String name, String imagePath) {
        this.categoryId = categoryId;
        this.name = name;
        this.imagePath = imagePath;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}








