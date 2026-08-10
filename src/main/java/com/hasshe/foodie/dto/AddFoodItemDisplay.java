package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.FoodItemConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddFoodItemDisplay(
        @NotBlank @Size(max = FoodItemConstants.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = FoodItemConstants.DISH_CATEGORY_MAX_LENGTH) String dishCategory
) {}
