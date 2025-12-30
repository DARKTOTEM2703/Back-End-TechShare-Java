package com.techmate.techmate.infra.adapter.output.jpa;

import com.techmate.techmate.domain.model.material.Category;
import com.techmate.techmate.domain.port.out.CategoryRepositoryPort;
import com.techmate.techmate.hexagonal.domain.entity.Categories;
import com.techmate.techmate.infra.mapper.DomainMaterialMapper;
import com.techmate.techmate.hexagonal.domain.repository.CategoriesRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 🔌 JPA ADAPTER - JpaCategoryRepositoryAdapter
 * 
 * Implementa CategoryRepositoryPort usando JPA.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Component
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoriesRepository jpaRepository;
    private final DomainMaterialMapper mapper;

    public JpaCategoryRepositoryAdapter(
            CategoriesRepository jpaRepository,
            DomainMaterialMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        Categories entity = mapper.toEntity(category);
        Categories saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findByName(String name) {
        Categories entity = jpaRepository.findByName(name);
        return entity != null ? Optional.of(mapper.toDomain(entity)) : Optional.empty();
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
    public boolean existsByName(String name) {
        return jpaRepository.findByName(name) != null;
    }
}
