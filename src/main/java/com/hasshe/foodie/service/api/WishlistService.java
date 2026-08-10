package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;

import java.util.List;

public interface WishlistService {

    RestaurantDomain addToWishlist(String username, AddRestaurantDisplay addRestaurantDisplay);

    List<RestaurantDomain> listWishlistForUser(String username);

    RestaurantDomain checkOffWishlistItem(String username, Long restaurantId);
}
