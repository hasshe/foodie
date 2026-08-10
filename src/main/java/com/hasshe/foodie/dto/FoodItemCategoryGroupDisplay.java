package com.hasshe.foodie.dto;

import java.util.List;

public record FoodItemCategoryGroupDisplay(
        String dishCategory,
        List<FoodItemWithRestaurantDisplay> foodItems
) {}
