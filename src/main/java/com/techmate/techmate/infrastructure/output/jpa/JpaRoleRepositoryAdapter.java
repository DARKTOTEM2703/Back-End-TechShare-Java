package com.techmate.techmate.infrastructure.adapter.output.jpa;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.domain.model.user.Role;
import com.techmate.techmate.domain.port.out.RoleRepositoryPort;
import com.techmate.techmate.infrastructure.mapper.DomainUserMapper;
import com.techmate.techmate.domain.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter for Role persistence operations.
 * 
 * Maps between domain Role model and JPA Role entity.
 * Implements RoleRepositoryPort interface.
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
    public Optional<Role> findById(Integer id) {
        logger.debug("Finding role by id: {}", id);
        return roleRepository.findById(id)
                .map(roleMapper::roleToDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        logger.debug("Finding role by name: {}", name);
        return roleRepository.findByNameIgnoreCase(name)
                .map(roleMapper::roleToDomain);
    }

    @Override
    public List<Role> findAll() {
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
    public Role save(Role role) {
        logger.debug("Saving role: {}", role.getName());
        com.techmate.techmate.domain.entity.Role roleEntity = roleMapper.roleToEntity(role);
        com.techmate.techmate.domain.entity.Role savedEntity = roleRepository.save(roleEntity);
        return roleMapper.roleToDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        logger.debug("Deleting role by id: {}", id);
        roleRepository.deleteById(id);
    }
}
