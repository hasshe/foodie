package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.domain.FoodItemCategoryGroupDomain;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.domain.FoodItemWithRestaurantDomain;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemWithRestaurantDisplay;

public interface FoodItemMapper {

    FoodItemDomain mapToDomain(FoodItemEntity foodItemEntity, double averageRating, int ratingCount);

    FoodItemDisplay mapToDisplay(FoodItemDomain foodItemDomain);

    FoodItemWithRestaurantDisplay mapToDisplay(FoodItemWithRestaurantDomain foodItemWithRestaurantDomain);

    FoodItemCategoryGroupDisplay mapToDisplay(FoodItemCategoryGroupDomain foodItemCategoryGroupDomain);
}
