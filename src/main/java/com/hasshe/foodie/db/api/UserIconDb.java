package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.UserIconEntity;

import java.util.List;
import java.util.Optional;

public interface UserIconDb {

    UserIconEntity save(UserIconEntity userIconEntity);

    List<UserIconEntity> findAll();

    Optional<UserIconEntity> findById(Long id);
}
