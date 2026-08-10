package com.hasshe.foodie.domain;

import java.util.List;

public record RestaurantRatingSummaryDomain(
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
        List<RestaurantRatingDomain> ratings
) {}
