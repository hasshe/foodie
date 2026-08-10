package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record RestaurantDomain(
        Long id,
        String name,
        String address,
        String cuisineType,
        String website,
        String phone,
        GroupDomain group,
        double averageRating,
        int ratingCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
