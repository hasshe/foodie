package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.UserIconDb;
import com.hasshe.foodie.db.entity.UserIconEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class UserIconDbImpl implements UserIconDb {

    private static final Logger log = LoggerFactory.getLogger(UserIconDbImpl.class);

    private final UserIconJpaRepository userIconJpaRepository;

    UserIconDbImpl(UserIconJpaRepository userIconJpaRepository) {
        this.userIconJpaRepository = userIconJpaRepository;
    }

    @Override
    public UserIconEntity save(UserIconEntity userIconEntity) {
        Assert.notNull(userIconEntity, "userIconEntity must not be null");
        log.debug("Saving user icon with key {}", userIconEntity.getIconKey());
        UserIconEntity saved = userIconJpaRepository.save(userIconEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public List<UserIconEntity> findAll() {
        log.debug("Finding all user icons");
        return userIconJpaRepository.findAll();
    }

    @Override
    public Optional<UserIconEntity> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Finding user icon by id {}", id);
        return userIconJpaRepository.findById(id);
    }
}
