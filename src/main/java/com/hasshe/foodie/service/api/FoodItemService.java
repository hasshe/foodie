package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;

import java.util.List;

public interface FoodItemService {

    FoodItemDomain addFoodItem(String username, Long restaurantId, AddFoodItemDisplay addFoodItemDisplay);

    List<FoodItemDomain> listFoodItemsForRestaurant(String username, Long restaurantId);
}
