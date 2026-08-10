package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.RestaurantRatingConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateRestaurantDisplay(
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer employeesService,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer audioMusic,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer generalVibes,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer priceForQuality,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer locationLocale,
        @NotNull @Min(RestaurantRatingConstants.MIN_SCORE) @Max(RestaurantRatingConstants.MAX_SCORE) Integer foodQuality
) {}
