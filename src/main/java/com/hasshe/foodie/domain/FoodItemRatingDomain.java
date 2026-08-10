package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record FoodItemRatingDomain(
        Long id,
        Long foodItemId,
        String raterUsername,
        String raterDisplayName,
        int taste,
        int presentation,
        int portionQuality,
        int valueForPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public double averageScore() {
        return (taste + presentation + portionQuality + valueForPrice) / 4.0;
    }
}
