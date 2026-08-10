package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record RestaurantRatingDomain(
        Long id,
        Long restaurantId,
        String raterUsername,
        String raterDisplayName,
        int food,
        int service,
        int vibe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public double averageScore() {
        return (food + service + vibe) / 3.0;
    }
}
