package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface FoodItemRatingJpaRepository extends JpaRepository<FoodItemRatingEntity, Long> {

    @Query("SELECT r FROM FoodItemRatingEntity r WHERE r.foodItem.id = :foodItemId AND r.rater.id = :userId")
    Optional<FoodItemRatingEntity> findByFoodItemIdAndUserId(@Param("foodItemId") Long foodItemId, @Param("userId") Long userId);

    @Query("SELECT r FROM FoodItemRatingEntity r WHERE r.foodItem.id = :foodItemId")
    List<FoodItemRatingEntity> findByFoodItemId(@Param("foodItemId") Long foodItemId);
}
