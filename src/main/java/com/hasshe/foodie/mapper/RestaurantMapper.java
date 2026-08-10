package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.RestaurantDisplay;

public interface RestaurantMapper {

    RestaurantDomain mapToDomain(RestaurantEntity restaurantEntity);

    RestaurantDisplay mapToDisplay(RestaurantDomain restaurantDomain);
}
