package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.FoodItemDisplay;

public interface FoodItemMapper {

    FoodItemDomain mapToDomain(FoodItemEntity foodItemEntity, double averageRating, int ratingCount);

    FoodItemDisplay mapToDisplay(FoodItemDomain foodItemDomain);
}
