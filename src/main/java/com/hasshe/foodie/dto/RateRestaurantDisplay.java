package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.RestaurantRatingConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateRestaurantDisplay(
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer food,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer service,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer vibe
) {}
