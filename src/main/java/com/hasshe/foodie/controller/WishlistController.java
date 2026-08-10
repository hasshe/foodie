package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.WishlistService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class WishlistController {

    private final WishlistService wishlistService;
    private final RestaurantMapper restaurantMapper;

    public WishlistController(WishlistService wishlistService, RestaurantMapper restaurantMapper) {
        this.wishlistService = wishlistService;
        this.restaurantMapper = restaurantMapper;
    }

    public RestaurantDisplay addToWishlist(String username, AddRestaurantDisplay addRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(addRestaurantDisplay, "addRestaurantDisplay must not be null");
        RestaurantDomain restaurantDomain = wishlistService.addToWishlist(username, addRestaurantDisplay);
        return restaurantMapper.mapToDisplay(restaurantDomain);
    }

    public List<RestaurantDisplay> listWishlistForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        return wishlistService.listWishlistForUser(username).stream().map(restaurantMapper::mapToDisplay).toList();
    }

    public RestaurantDisplay checkOffWishlistItem(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        RestaurantDomain restaurantDomain = wishlistService.checkOffWishlistItem(username, restaurantId);
        return restaurantMapper.mapToDisplay(restaurantDomain);
    }
}
