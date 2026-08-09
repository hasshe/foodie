package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.UserEntity;

import java.util.Optional;

public interface UserDb {

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
