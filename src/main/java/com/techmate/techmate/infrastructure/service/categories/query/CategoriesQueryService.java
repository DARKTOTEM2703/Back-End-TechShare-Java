package com.techmate.techmate.infrastructure.service.categories.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.techmate.techmate.infrastructure.dto.CategoriesDTO;
import com.techmate.techmate.infrastructure.persistence.entity.Categories;
import com.techmate.techmate.infrastructure.persistence.repository.CategoriesRepository;
import com.techmate.techmate.infrastructure.service.categories.mapper.CategoriesMapper;

@Component
public class CategoriesQueryService {

    private final CategoriesRepository categoriesRepository;
    private final CategoriesMapper categoriesMapper;

    public CategoriesQueryService(CategoriesRepository categoriesRepository, CategoriesMapper categoriesMapper) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesMapper = categoriesMapper;
    }

    public List<CategoriesDTO> getAll() {
        return categoriesRepository.findAll().stream().map(categoriesMapper::toDTO).collect(Collectors.toList());
    }

    public CategoriesDTO getById(int id) {
        Categories c = categoriesRepository.findById(id).orElse(null);
        return categoriesMapper.toDTO(c);
    }
}









