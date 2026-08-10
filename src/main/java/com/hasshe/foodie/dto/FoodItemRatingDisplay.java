package com.hasshe.foodie.dto;

public record FoodItemRatingDisplay(
        Long id,
        String raterDisplayName,
        int rating
) {}
