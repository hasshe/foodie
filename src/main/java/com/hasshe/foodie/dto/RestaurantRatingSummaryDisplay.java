package com.hasshe.foodie.dto;

import java.util.List;

public record RestaurantRatingSummaryDisplay(
        Long restaurantId,
        String restaurantName,
        double averageFood,
        double averageService,
        double averageVibe,
        double overallAverage,
        int ratingCount,
        List<RestaurantRatingDisplay> ratings,
        RestaurantRatingDisplay currentUserRating
) {}
