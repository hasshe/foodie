package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class FoodItemRatingDbImpl implements FoodItemRatingDb {

    private static final Logger log = LoggerFactory.getLogger(FoodItemRatingDbImpl.class);

    private final FoodItemRatingJpaRepository foodItemRatingJpaRepository;

    FoodItemRatingDbImpl(FoodItemRatingJpaRepository foodItemRatingJpaRepository) {
        this.foodItemRatingJpaRepository = foodItemRatingJpaRepository;
    }

    @Override
    public FoodItemRatingEntity save(FoodItemRatingEntity foodItemRatingEntity) {
        Assert.notNull(foodItemRatingEntity, "foodItemRatingEntity must not be null");
        log.debug("Saving food item rating for food item id {}", foodItemRatingEntity.getFoodItem().getId());
        FoodItemRatingEntity saved = foodItemRatingJpaRepository.save(foodItemRatingEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<FoodItemRatingEntity> findByFoodItemIdAndUserId(Long foodItemId, Long userId) {
        Assert.notNull(foodItemId, "foodItemId must not be null");
        Assert.notNull(userId, "userId must not be null");
        log.debug("Finding food item rating for food item id {} and user id {}", foodItemId, userId);
        return foodItemRatingJpaRepository.findByFoodItemIdAndUserId(foodItemId, userId);
    }

    @Override
    public List<FoodItemRatingEntity> findByFoodItemId(Long foodItemId) {
        Assert.notNull(foodItemId, "foodItemId must not be null");
        log.debug("Finding food item ratings for food item id {}", foodItemId);
        return foodItemRatingJpaRepository.findByFoodItemId(foodItemId);
    }
}
