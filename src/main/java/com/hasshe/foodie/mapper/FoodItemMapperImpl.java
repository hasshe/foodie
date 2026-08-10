package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.domain.FoodItemCategoryGroupDomain;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.domain.FoodItemWithRestaurantDomain;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemWithRestaurantDisplay;
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

    @Override
    public FoodItemWithRestaurantDisplay mapToDisplay(FoodItemWithRestaurantDomain foodItemWithRestaurantDomain) {
        Assert.notNull(foodItemWithRestaurantDomain, "foodItemWithRestaurantDomain must not be null");
        FoodItemWithRestaurantDisplay foodItemWithRestaurantDisplay = new FoodItemWithRestaurantDisplay(
                foodItemWithRestaurantDomain.id(),
                foodItemWithRestaurantDomain.name(),
                foodItemWithRestaurantDomain.restaurantName(),
                foodItemWithRestaurantDomain.averageRating(),
                foodItemWithRestaurantDomain.ratingCount()
        );
        assert foodItemWithRestaurantDisplay != null : "mapping must never produce null";
        return foodItemWithRestaurantDisplay;
    }

    @Override
    public FoodItemCategoryGroupDisplay mapToDisplay(FoodItemCategoryGroupDomain foodItemCategoryGroupDomain) {
        Assert.notNull(foodItemCategoryGroupDomain, "foodItemCategoryGroupDomain must not be null");
        FoodItemCategoryGroupDisplay foodItemCategoryGroupDisplay = new FoodItemCategoryGroupDisplay(
                foodItemCategoryGroupDomain.dishCategory(),
                foodItemCategoryGroupDomain.foodItems().stream().map(this::mapToDisplay).toList()
        );
        assert foodItemCategoryGroupDisplay != null : "mapping must never produce null";
        return foodItemCategoryGroupDisplay;
    }
}
