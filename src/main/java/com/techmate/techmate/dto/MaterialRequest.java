package com.techmate.techmate.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitudes de creación/actualización de materiales.
 */
public class MaterialRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    private String description;

    @NotNull
    private Integer stock;

    private Double price;

    private Integer subCategoryId;

    private List<Integer> roleIds;

    public MaterialRequest() {
    }

    // getters / setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getSubCategoryId() { return subCategoryId; }
    public void setSubCategoryId(Integer subCategoryId) { this.subCategoryId = subCategoryId; }
    public List<Integer> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Integer> roleIds) { this.roleIds = roleIds; }
}

