package com.hasshe.foodie.dto;

import java.util.List;

public record FoodItemRatingSummaryDisplay(
        Long foodItemId,
        String foodItemName,
        double averageRating,
        int ratingCount,
        List<FoodItemRatingDisplay> ratings,
        FoodItemRatingDisplay currentUserRating
) {}
