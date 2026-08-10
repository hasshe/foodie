package com.hasshe.foodie.dto;

public record RestaurantDisplay(
        Long id,
        String name,
        String address,
        String cuisineType,
        String website,
        String phone,
        String groupName,
        double averageRating,
        int ratingCount
) {}
