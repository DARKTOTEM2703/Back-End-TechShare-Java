package com.techmate.techmate.hexagonal.infrastructure.service.categories.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.CategoryRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.CategoryResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.CategoriesDTO;
import com.techmate.techmate.hexagonal.domain.entity.Categories;

@Component
public class CategoriesMapper {

    // API -> internal DTO
    public CategoriesDTO fromRequest(CategoryRequest req) {
        if (req == null) return null;
        CategoriesDTO dto = new CategoriesDTO();
        dto.setName(req.getName());
        return dto;
    }

    // internal DTO -> API response
    public CategoryResponse toResponse(CategoriesDTO dto, String serverUrl) {
        if (dto == null) return null;
        String image = dto.getImagePath();
        if (image != null && serverUrl != null && !serverUrl.isEmpty() && !image.startsWith("http")) {
            image = serverUrl + "/admin/categories/images/" + image;
        }
        return new CategoryResponse(dto.getId(), dto.getName(), image);
    }

    // Entity <-> DTO (kept for services that may use it)
    public CategoriesDTO toDTO(Categories c) {
        if (c == null) return null;
        CategoriesDTO dto = new CategoriesDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setImagePath(c.getImagePath());
        return dto;
    }

    public Categories toEntity(CategoriesDTO dto) {
        if (dto == null) return null;
        Categories c = new Categories();
        c.setId(dto.getId());
        c.setName(dto.getName());
        c.setImagePath(dto.getImagePath());
        return c;
    }
}








