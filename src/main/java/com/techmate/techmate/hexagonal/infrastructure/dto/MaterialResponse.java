package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.util.List;

/**
 * DTO público para respuestas de materiales.
 */
public class MaterialResponse {
    private int materialsId;
    private String imagePath;
    private String name;
    private String description;
    private java.math.BigDecimal price;
    private int stock;
    private int borrowable_stock;
    private int subCategoryId;
    private String subCategoryName;
    private List<String> roleNames;

    public MaterialResponse() {
    }

    public MaterialResponse(int materialsId, String imagePath, String name, String description,
            java.math.BigDecimal price, int stock,
            int borrowable_stock, int subCategoryId, String subCategoryName, List<String> roleNames) {
        this.materialsId = materialsId;
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.borrowable_stock = borrowable_stock;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.roleNames = roleNames;
    }

    // getters / setters
    public int getMaterialsId() {
        return materialsId;
    }

    public void setMaterialsId(int materialsId) {
        this.materialsId = materialsId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getBorrowable_stock() {
        return borrowable_stock;
    }

    public void setBorrowable_stock(int borrowable_stock) {
        this.borrowable_stock = borrowable_stock;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }
}


