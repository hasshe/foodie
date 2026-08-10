package com.hasshe.foodie.domain;

public record FoodItemWithRestaurantDomain(
        Long id,
        String name,
        String dishCategory,
        String restaurantName,
        double averageRating,
        int ratingCount
) {}
