package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record RestaurantRatingDomain(
        Long id,
        Long restaurantId,
        String raterUsername,
        String raterDisplayName,
        int employeesService,
        int audioMusic,
        int generalVibes,
        int priceForQuality,
        int locationLocale,
        int foodQuality,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public double averageScore() {
        return (employeesService + audioMusic + generalVibes + priceForQuality + locationLocale + foodQuality) / 6.0;
    }
}
