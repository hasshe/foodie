package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.RestaurantRatingEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantRatingDb {

    RestaurantRatingEntity save(RestaurantRatingEntity restaurantRatingEntity);

    Optional<RestaurantRatingEntity> findByRestaurantIdAndUserId(Long restaurantId, Long userId);

    List<RestaurantRatingEntity> findByRestaurantId(Long restaurantId);
}
