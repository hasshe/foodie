package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.RestaurantConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddRestaurantDisplay(
        @NotBlank @Size(max = RestaurantConstants.NAME_MAX_LENGTH) String name,
        @NotBlank @Size(max = RestaurantConstants.ADDRESS_MAX_LENGTH) String address,
        @Size(max = RestaurantConstants.CUISINE_TYPE_MAX_LENGTH) String cuisineType,
        @Size(max = RestaurantConstants.WEBSITE_MAX_LENGTH) String website,
        @Size(max = RestaurantConstants.PHONE_MAX_LENGTH) String phone,
        @NotNull Long groupId
) {}
