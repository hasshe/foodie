package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.FoodItemRatingEntity;

import java.util.List;
import java.util.Optional;

public interface FoodItemRatingDb {

    FoodItemRatingEntity save(FoodItemRatingEntity foodItemRatingEntity);

    Optional<FoodItemRatingEntity> findByFoodItemIdAndUserId(Long foodItemId, Long userId);

    List<FoodItemRatingEntity> findByFoodItemId(Long foodItemId);
}
