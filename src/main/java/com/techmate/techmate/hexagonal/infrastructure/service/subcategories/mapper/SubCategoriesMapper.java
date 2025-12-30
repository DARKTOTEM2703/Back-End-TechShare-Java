package com.techmate.techmate.hexagonal.infrastructure.service.subcategories.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoryRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoryResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoriesDTO;

@Component
public class SubCategoriesMapper {

    public SubCategoriesDTO fromRequest(SubCategoryRequest req) {
        if (req == null) return null;
        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setName(req.getName());
        dto.setId(req.getId());
        return dto;
    }

    public SubCategoryResponse toResponse(SubCategoriesDTO dto, String serverUrl) {
        if (dto == null) return null;
        String image = dto.getImagePath();
        if (image != null && serverUrl != null && !serverUrl.isEmpty() && !image.startsWith("http")) {
            image = serverUrl + "/admin/subcategories/images/" + image;
        }
        return new SubCategoryResponse(dto.getId(), dto.getName(), image, dto.getId(), dto.getCategoryName());
    }
}








