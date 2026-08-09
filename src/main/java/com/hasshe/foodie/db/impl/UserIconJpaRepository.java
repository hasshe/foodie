package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.UserIconEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserIconJpaRepository extends JpaRepository<UserIconEntity, Long> {
}
