package com.hasshe.foodie.domain;

import java.util.List;

public record FoodItemRatingSummaryDomain(
        Long foodItemId,
        String foodItemName,
        double averageRating,
        int ratingCount,
        List<FoodItemRatingDomain> ratings
) {}
