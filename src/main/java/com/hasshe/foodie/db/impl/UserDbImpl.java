package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Optional;

@Repository
class UserDbImpl implements UserDb {

    private static final Logger log = LoggerFactory.getLogger(UserDbImpl.class);

    private final UserJpaRepository userJpaRepository;

    UserDbImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        Assert.notNull(userEntity, "userEntity must not be null");
        log.debug("Saving user with username {}", userEntity.getUsername());
        UserEntity saved = userJpaRepository.save(userEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Finding user by id {}", id);
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Finding user by username {}", username);
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Checking existence of user with username {}", username);
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(id, "id must not be null");
        log.debug("Checking existence of user with username {} excluding id {}", username, id);
        return userJpaRepository.existsByUsernameAndIdNot(username, id);
    }
}
