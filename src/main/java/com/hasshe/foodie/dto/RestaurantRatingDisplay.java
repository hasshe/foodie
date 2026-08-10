package com.hasshe.foodie.dto;

public record RestaurantRatingDisplay(
        Long id,
        String raterDisplayName,
        int employeesService,
        int audioMusic,
        int generalVibes,
        int priceForQuality,
        int locationLocale,
        int foodQuality,
        double averageScore
) {}
