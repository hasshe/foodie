package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;

import java.util.List;

public interface RestaurantRatingMapper {

    RestaurantRatingDomain mapToDomain(RestaurantRatingEntity restaurantRatingEntity);

    RestaurantRatingDisplay mapToDisplay(RestaurantRatingDomain restaurantRatingDomain);

    RestaurantRatingSummaryDomain mapToSummaryDomain(RestaurantEntity restaurantEntity, List<RestaurantRatingEntity> restaurantRatingEntities);

    RestaurantRatingSummaryDisplay mapToSummaryDisplay(RestaurantRatingSummaryDomain restaurantRatingSummaryDomain, String requestingUsername);
}
