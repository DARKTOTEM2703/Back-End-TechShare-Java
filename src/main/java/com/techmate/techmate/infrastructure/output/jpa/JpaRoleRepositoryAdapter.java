package com.techmate.techmate.infrastructure.output.jpa;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.core.application.port.output.RoleRepositoryPort;
import com.techmate.techmate.mapper.DomainUserMapper;
import com.techmate.techmate.infrastructure.persistence.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for Role persistence operations.
 * 
 * Maps between domain Role model and JPA Role entity.
 * Implements RoleRepositoryPort interface (uses domain models, not JPA
 * entities).
 */
@Component
@Transactional(readOnly = true)
public class JpaRoleRepositoryAdapter implements RoleRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(JpaRoleRepositoryAdapter.class);

    private final RoleRepository roleRepository;
    private final DomainUserMapper roleMapper;

    public JpaRoleRepositoryAdapter(RoleRepository roleRepository, DomainUserMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<com.techmate.techmate.core.domain.model.user.Role> findById(Integer id) {
        logger.debug("Finding role by id: {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::roleToDomain);
    }

    @Override
    public Optional<com.techmate.techmate.core.domain.model.user.Role> findByName(String name) {
        logger.debug("Finding role by name: {}", name);
        return roleRepository.findByNameIgnoreCase(name)
                .map(roleMapper::roleToDomain);
    }

    @Override
    public List<com.techmate.techmate.core.domain.model.user.Role> findAll() {
        logger.debug("Finding all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::roleToDomain)
                .toList();
    }

    @Override
    public boolean existsById(Integer id) {
        logger.debug("Checking role existence by id: {}", id);
        return roleRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        logger.debug("Checking role existence by name: {}", name);
        return roleRepository.findByNameIgnoreCase(name).isPresent();
    }

    @Override
    public long count() {
        logger.debug("Counting total roles");
        return roleRepository.count();
    }

    @Override
    @Transactional
    public com.techmate.techmate.core.domain.model.user.Role save(
            com.techmate.techmate.core.domain.model.user.Role role) {
        logger.debug("Saving role: {}", role.getName());
        com.techmate.techmate.infrastructure.persistence.entity.Role roleEntity = roleMapper.roleToEntity(role);
        com.techmate.techmate.infrastructure.persistence.entity.Role savedEntity = roleRepository.save(roleEntity);
        return roleMapper.roleToDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        logger.debug("Deleting role by id: {}", id);
        roleRepository.deleteById(id);
    }
}
