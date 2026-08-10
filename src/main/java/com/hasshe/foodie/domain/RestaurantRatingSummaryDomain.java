package com.hasshe.foodie.domain;

import java.util.List;

public record RestaurantRatingSummaryDomain(
        Long restaurantId,
        String restaurantName,
        double averageFood,
        double averageService,
        double averageVibe,
        double overallAverage,
        int ratingCount,
        List<RestaurantRatingDomain> ratings
) {}
