package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.FoodItemRatingConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateFoodItemDisplay(
        @NotNull @Min(FoodItemRatingConstants.MIN_SCORE) @Max(FoodItemRatingConstants.MAX_SCORE) Integer taste,
        @NotNull @Min(FoodItemRatingConstants.MIN_SCORE) @Max(FoodItemRatingConstants.MAX_SCORE) Integer presentation,
        @NotNull @Min(FoodItemRatingConstants.MIN_SCORE) @Max(FoodItemRatingConstants.MAX_SCORE) Integer portionQuality,
        @NotNull @Min(FoodItemRatingConstants.MIN_SCORE) @Max(FoodItemRatingConstants.MAX_SCORE) Integer valueForPrice
) {}
