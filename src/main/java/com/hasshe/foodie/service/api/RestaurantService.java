package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;

import java.util.List;

public interface RestaurantService {

    RestaurantDomain addRestaurant(String username, AddRestaurantDisplay addRestaurantDisplay);

    List<RestaurantDomain> listRestaurantsForUser(String username);
}
