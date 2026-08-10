package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface RestaurantRatingJpaRepository extends JpaRepository<RestaurantRatingEntity, Long> {

    @Query("SELECT r FROM RestaurantRatingEntity r WHERE r.restaurant.id = :restaurantId AND r.rater.id = :userId")
    Optional<RestaurantRatingEntity> findByRestaurantIdAndUserId(@Param("restaurantId") Long restaurantId, @Param("userId") Long userId);

    @Query("SELECT r FROM RestaurantRatingEntity r WHERE r.restaurant.id = :restaurantId")
    List<RestaurantRatingEntity> findByRestaurantId(@Param("restaurantId") Long restaurantId);
}
