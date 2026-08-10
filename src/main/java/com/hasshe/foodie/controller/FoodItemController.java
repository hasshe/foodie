package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.mapper.FoodItemMapper;
import com.hasshe.foodie.service.api.FoodItemService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class FoodItemController {

    private final FoodItemService foodItemService;
    private final FoodItemMapper foodItemMapper;

    public FoodItemController(FoodItemService foodItemService, FoodItemMapper foodItemMapper) {
        this.foodItemService = foodItemService;
        this.foodItemMapper = foodItemMapper;
    }

    public FoodItemDisplay addFoodItem(String username, Long restaurantId, AddFoodItemDisplay addFoodItemDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        Assert.notNull(addFoodItemDisplay, "addFoodItemDisplay must not be null");
        FoodItemDomain foodItemDomain = foodItemService.addFoodItem(username, restaurantId, addFoodItemDisplay);
        return foodItemMapper.mapToDisplay(foodItemDomain);
    }

    public List<FoodItemDisplay> listFoodItemsForRestaurant(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        return foodItemService.listFoodItemsForRestaurant(username, restaurantId).stream().map(foodItemMapper::mapToDisplay).toList();
    }
}
