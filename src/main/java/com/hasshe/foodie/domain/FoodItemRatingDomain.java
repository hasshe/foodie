package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record FoodItemRatingDomain(
        Long id,
        Long foodItemId,
        String raterUsername,
        String raterDisplayName,
        int rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
