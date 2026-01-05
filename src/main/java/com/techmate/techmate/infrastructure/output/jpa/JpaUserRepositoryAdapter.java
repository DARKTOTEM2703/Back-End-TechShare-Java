package com.techmate.techmate.infrastructure.output.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.core.domain.model.user.User;
import com.techmate.techmate.domain.port.out.UserRepositoryPort;
import com.techmate.techmate.mapper.DomainUserMapper;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRepository;
import com.techmate.techmate.infrastructure.persistence.entity.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA adapter for User persistence operations.
 * 
 * Maps between domain User model and JPA Usuario entity.
 * Implements UserRepositoryPort interface.
 */
@Component
@Transactional(readOnly = true)
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(JpaUserRepositoryAdapter.class);

    private final UsuarioRepository usuarioRepository;
    private final DomainUserMapper userMapper;

    public JpaUserRepositoryAdapter(UsuarioRepository usuarioRepository, DomainUserMapper userMapper) {
        this.usuarioRepository = usuarioRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(Integer id) {
        logger.debug("Finding user by id: {}", id);
        return usuarioRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return usuarioRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return usuarioRepository.findOneByEmailNative(email)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        logger.debug("Finding all users");
        return usuarioRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findEnabledUsers() {
        logger.debug("Finding all enabled users");
        return usuarioRepository.findByIsEnabledTrue().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findDisabledUsers() {
        logger.debug("Finding all disabled users");
        return usuarioRepository.findByIsEnabledFalse().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findByRole(String roleName) {
        logger.debug("Finding users by role: {}", roleName);
        return usuarioRepository.findByRoleNames(roleName).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllPaginated(int pageNumber, int pageSize) {
        logger.debug("Finding paginated users - page: {}, size: {}", pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return usuarioRepository.findAll(pageable).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Integer id) {
        logger.debug("Checking user existence by id: {}", id);
        return usuarioRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Checking user existence by username: {}", username);
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking user existence by email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public long count() {
        logger.debug("Counting total users");
        return usuarioRepository.count();
    }

    @Override
    @Transactional
    public User save(User user) {
        logger.debug("Saving user: {}", user.getUsername());
        Usuario usuarioEntity = userMapper.toEntity(user);
        Usuario savedEntity = usuarioRepository.save(usuarioEntity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        logger.debug("Deleting user by id: {}", id);
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateLastLoginTime(Integer userId) {
        logger.debug("Updating last login time for user: {}", userId);
        usuarioRepository.updateLastLoginTime(userId);
    }
}
