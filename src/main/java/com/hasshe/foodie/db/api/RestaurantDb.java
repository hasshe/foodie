package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantDb {

    RestaurantEntity save(RestaurantEntity restaurantEntity);

    Optional<RestaurantEntity> findById(Long id);

    List<RestaurantEntity> findByGroupIdInAndWishlist(List<Long> groupIds, boolean wishlist);
}
