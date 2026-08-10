package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Long> {

    @Query("SELECT r FROM RestaurantEntity r WHERE r.group.id IN :groupIds AND r.wishlist = :wishlist")
    List<RestaurantEntity> findByGroupIdInAndWishlist(@Param("groupIds") List<Long> groupIds, @Param("wishlist") boolean wishlist);
}
