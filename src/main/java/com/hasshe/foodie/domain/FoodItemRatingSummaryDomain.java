package com.hasshe.foodie.domain;

import java.util.List;

public record FoodItemRatingSummaryDomain(
        Long foodItemId,
        String foodItemName,
        double averageTaste,
        double averagePresentation,
        double averagePortionQuality,
        double averageValueForPrice,
        double overallAverage,
        int ratingCount,
        List<FoodItemRatingDomain> ratings
) {}
