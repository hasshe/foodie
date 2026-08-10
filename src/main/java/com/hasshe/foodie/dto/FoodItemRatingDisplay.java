package com.hasshe.foodie.dto;

public record FoodItemRatingDisplay(
        Long id,
        String raterDisplayName,
        int taste,
        int presentation,
        int portionQuality,
        int valueForPrice,
        double averageScore
) {}
