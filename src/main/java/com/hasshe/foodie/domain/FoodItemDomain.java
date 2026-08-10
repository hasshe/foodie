package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record FoodItemDomain(
        Long id,
        Long restaurantId,
        String name,
        String dishCategory,
        double averageRating,
        int ratingCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
