package com.hasshe.foodie.dto;

import java.util.List;

public record FoodItemRatingSummaryDisplay(
        Long foodItemId,
        String foodItemName,
        double averageTaste,
        double averagePresentation,
        double averagePortionQuality,
        double averageValueForPrice,
        double overallAverage,
        int ratingCount,
        List<FoodItemRatingDisplay> ratings,
        FoodItemRatingDisplay currentUserRating
) {}
