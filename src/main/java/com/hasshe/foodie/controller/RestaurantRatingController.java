package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import com.hasshe.foodie.mapper.RestaurantRatingMapper;
import com.hasshe.foodie.service.api.RestaurantRatingService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RestaurantRatingController {

    private final RestaurantRatingService restaurantRatingService;
    private final RestaurantRatingMapper restaurantRatingMapper;

    public RestaurantRatingController(RestaurantRatingService restaurantRatingService, RestaurantRatingMapper restaurantRatingMapper) {
        this.restaurantRatingService = restaurantRatingService;
        this.restaurantRatingMapper = restaurantRatingMapper;
    }

    public RestaurantRatingDisplay rateRestaurant(String username, Long restaurantId, RateRestaurantDisplay rateRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        Assert.notNull(rateRestaurantDisplay, "rateRestaurantDisplay must not be null");
        RestaurantRatingDomain restaurantRatingDomain = restaurantRatingService.rateRestaurant(username, restaurantId, rateRestaurantDisplay);
        return restaurantRatingMapper.mapToDisplay(restaurantRatingDomain);
    }

    public RestaurantRatingSummaryDisplay getRatingSummary(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        RestaurantRatingSummaryDomain restaurantRatingSummaryDomain = restaurantRatingService.getRatingSummary(username, restaurantId);
        return restaurantRatingMapper.mapToSummaryDisplay(restaurantRatingSummaryDomain, username);
    }
}
