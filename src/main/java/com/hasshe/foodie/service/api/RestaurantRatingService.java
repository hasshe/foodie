package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RateRestaurantDisplay;

public interface RestaurantRatingService {

    RestaurantRatingDomain rateRestaurant(String username, Long restaurantId, RateRestaurantDisplay rateRestaurantDisplay);

    RestaurantRatingSummaryDomain getRatingSummary(String username, Long restaurantId);
}
