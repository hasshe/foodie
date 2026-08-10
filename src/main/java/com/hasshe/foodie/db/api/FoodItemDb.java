package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.FoodItemEntity;

import java.util.List;
import java.util.Optional;

public interface FoodItemDb {

    FoodItemEntity save(FoodItemEntity foodItemEntity);

    Optional<FoodItemEntity> findById(Long id);

    List<FoodItemEntity> findByRestaurantId(Long restaurantId);
}
