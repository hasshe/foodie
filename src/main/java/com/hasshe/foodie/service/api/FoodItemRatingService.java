package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.RateFoodItemDisplay;

public interface FoodItemRatingService {

    FoodItemRatingDomain rateFoodItem(String username, Long foodItemId, RateFoodItemDisplay rateFoodItemDisplay);

    FoodItemRatingSummaryDomain getRatingSummary(String username, Long foodItemId);
}
