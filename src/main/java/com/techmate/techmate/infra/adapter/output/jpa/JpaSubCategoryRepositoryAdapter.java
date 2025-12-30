package com.techmate.techmate.infra.adapter.output.jpa;

import com.techmate.techmate.domain.model.material.SubCategory;
import com.techmate.techmate.domain.port.out.SubCategoryRepositoryPort;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.infra.mapper.DomainMaterialMapper;
import com.techmate.techmate.repository.SubCategoriesRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 🔌 JPA ADAPTER - JpaSubCategoryRepositoryAdapter
 * 
 * Implementa SubCategoryRepositoryPort usando JPA.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Component
public class JpaSubCategoryRepositoryAdapter implements SubCategoryRepositoryPort {

    private final SubCategoriesRepository jpaRepository;
    private final DomainMaterialMapper mapper;

    public JpaSubCategoryRepositoryAdapter(
            SubCategoriesRepository jpaRepository,
            DomainMaterialMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SubCategory save(SubCategory subCategory) {
        SubCategories entity = mapper.toEntity(subCategory);
        SubCategories saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SubCategory> findById(Integer id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<SubCategory> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubCategory> findByCategoryId(Integer categoryId) {
        return jpaRepository.findByCategoryId(categoryId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<SubCategory> findByName(String name) {
        return jpaRepository.findByName(name)
            .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByNameAndCategoryId(String name, Integer categoryId) {
        return jpaRepository.existsByNameAndCategoryId(name, categoryId);
    }
}
