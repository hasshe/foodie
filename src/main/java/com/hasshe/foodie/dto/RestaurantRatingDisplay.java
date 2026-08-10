package com.hasshe.foodie.dto;

public record RestaurantRatingDisplay(
        Long id,
        String raterDisplayName,
        int food,
        int service,
        int vibe,
        double averageScore
) {}
