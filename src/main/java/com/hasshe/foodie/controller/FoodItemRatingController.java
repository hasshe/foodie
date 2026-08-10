package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.hasshe.foodie.mapper.FoodItemRatingMapper;
import com.hasshe.foodie.service.api.FoodItemRatingService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class FoodItemRatingController {

    private final FoodItemRatingService foodItemRatingService;
    private final FoodItemRatingMapper foodItemRatingMapper;

    public FoodItemRatingController(FoodItemRatingService foodItemRatingService, FoodItemRatingMapper foodItemRatingMapper) {
        this.foodItemRatingService = foodItemRatingService;
        this.foodItemRatingMapper = foodItemRatingMapper;
    }

    public FoodItemRatingDisplay rateFoodItem(String username, Long foodItemId, RateFoodItemDisplay rateFoodItemDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(foodItemId, "foodItemId must not be null");
        Assert.notNull(rateFoodItemDisplay, "rateFoodItemDisplay must not be null");
        FoodItemRatingDomain foodItemRatingDomain = foodItemRatingService.rateFoodItem(username, foodItemId, rateFoodItemDisplay);
        return foodItemRatingMapper.mapToDisplay(foodItemRatingDomain);
    }

    public FoodItemRatingSummaryDisplay getRatingSummary(String username, Long foodItemId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(foodItemId, "foodItemId must not be null");
        FoodItemRatingSummaryDomain foodItemRatingSummaryDomain = foodItemRatingService.getRatingSummary(username, foodItemId);
        return foodItemRatingMapper.mapToSummaryDisplay(foodItemRatingSummaryDomain, username);
    }
}
