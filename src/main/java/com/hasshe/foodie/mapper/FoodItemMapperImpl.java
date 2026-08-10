package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.FoodItemDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class FoodItemMapperImpl implements FoodItemMapper {

    @Override
    public FoodItemDomain mapToDomain(FoodItemEntity foodItemEntity, double averageRating, int ratingCount) {
        Assert.notNull(foodItemEntity, "foodItemEntity must not be null");
        FoodItemDomain foodItemDomain = new FoodItemDomain(
                foodItemEntity.getId(),
                foodItemEntity.getRestaurant().getId(),
                foodItemEntity.getName(),
                foodItemEntity.getDishCategory(),
                averageRating,
                ratingCount,
                foodItemEntity.getCreatedAt(),
                foodItemEntity.getUpdatedAt()
        );
        assert foodItemDomain != null : "mapping must never produce null";
        return foodItemDomain;
    }

    @Override
    public FoodItemDisplay mapToDisplay(FoodItemDomain foodItemDomain) {
        Assert.notNull(foodItemDomain, "foodItemDomain must not be null");
        FoodItemDisplay foodItemDisplay = new FoodItemDisplay(
                foodItemDomain.id(),
                foodItemDomain.name(),
                foodItemDomain.dishCategory(),
                foodItemDomain.averageRating(),
                foodItemDomain.ratingCount()
        );
        assert foodItemDisplay != null : "mapping must never produce null";
        return foodItemDisplay;
    }
}
