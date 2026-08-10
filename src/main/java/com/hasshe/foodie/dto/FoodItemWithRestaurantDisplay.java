package com.hasshe.foodie.dto;

public record FoodItemWithRestaurantDisplay(
        Long id,
        String name,
        String restaurantName,
        double averageRating,
        int ratingCount
) {}
