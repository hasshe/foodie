package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;

import java.util.List;

public interface FoodItemRatingMapper {

    FoodItemRatingDomain mapToDomain(FoodItemRatingEntity foodItemRatingEntity);

    FoodItemRatingDisplay mapToDisplay(FoodItemRatingDomain foodItemRatingDomain);

    FoodItemRatingSummaryDomain mapToSummaryDomain(FoodItemEntity foodItemEntity, List<FoodItemRatingEntity> foodItemRatingEntities);

    FoodItemRatingSummaryDisplay mapToSummaryDisplay(FoodItemRatingSummaryDomain foodItemRatingSummaryDomain, String requestingUsername);
}
