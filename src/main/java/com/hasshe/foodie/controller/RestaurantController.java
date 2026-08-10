package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.RestaurantService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    public RestaurantController(RestaurantService restaurantService, RestaurantMapper restaurantMapper) {
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
    }

    public RestaurantDisplay addRestaurant(String username, AddRestaurantDisplay addRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(addRestaurantDisplay, "addRestaurantDisplay must not be null");
        RestaurantDomain restaurantDomain = restaurantService.addRestaurant(username, addRestaurantDisplay);
        return restaurantMapper.mapToDisplay(restaurantDomain);
    }

    public List<RestaurantDisplay> listRestaurantsForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        return restaurantService.listRestaurantsForUser(username).stream().map(restaurantMapper::mapToDisplay).toList();
    }
}
