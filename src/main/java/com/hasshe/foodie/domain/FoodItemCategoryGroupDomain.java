package com.hasshe.foodie.domain;

import java.util.List;

public record FoodItemCategoryGroupDomain(
        String dishCategory,
        List<FoodItemWithRestaurantDomain> foodItems
) {}
