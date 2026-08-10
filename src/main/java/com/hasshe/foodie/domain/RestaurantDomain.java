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
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
