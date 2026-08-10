package com.hasshe.foodie.dto;

import java.util.List;

public record RestaurantRatingSummaryDisplay(
        Long restaurantId,
        String restaurantName,
        double averageEmployeesService,
        double averageAudioMusic,
        double averageGeneralVibes,
        double averagePriceForQuality,
        double averageLocationLocale,
        double averageFoodQuality,
        double overallAverage,
        int ratingCount,
        List<RestaurantRatingDisplay> ratings,
        RestaurantRatingDisplay currentUserRating
) {}
