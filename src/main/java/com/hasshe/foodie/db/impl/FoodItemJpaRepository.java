package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface FoodItemJpaRepository extends JpaRepository<FoodItemEntity, Long> {

    @Query("SELECT f FROM FoodItemEntity f WHERE f.restaurant.id = :restaurantId")
    List<FoodItemEntity> findByRestaurantId(@Param("restaurantId") Long restaurantId);
}
