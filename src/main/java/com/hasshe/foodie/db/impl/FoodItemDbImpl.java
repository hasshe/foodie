package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class FoodItemDbImpl implements FoodItemDb {

    private static final Logger log = LoggerFactory.getLogger(FoodItemDbImpl.class);

    private final FoodItemJpaRepository foodItemJpaRepository;

    FoodItemDbImpl(FoodItemJpaRepository foodItemJpaRepository) {
        this.foodItemJpaRepository = foodItemJpaRepository;
    }

    @Override
    public FoodItemEntity save(FoodItemEntity foodItemEntity) {
        Assert.notNull(foodItemEntity, "foodItemEntity must not be null");
        log.debug("Saving food item with name {}", foodItemEntity.getName());
        FoodItemEntity saved = foodItemJpaRepository.save(foodItemEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<FoodItemEntity> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Finding food item by id {}", id);
        return foodItemJpaRepository.findById(id);
    }

    @Override
    public List<FoodItemEntity> findByRestaurantId(Long restaurantId) {
        Assert.notNull(restaurantId, "restaurantId must not be null");
        log.debug("Finding food items for restaurant id {}", restaurantId);
        return foodItemJpaRepository.findByRestaurantId(restaurantId);
    }
}
