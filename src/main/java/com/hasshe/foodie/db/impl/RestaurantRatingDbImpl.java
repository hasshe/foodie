package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.RestaurantRatingDb;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class RestaurantRatingDbImpl implements RestaurantRatingDb {

    private static final Logger log = LoggerFactory.getLogger(RestaurantRatingDbImpl.class);

    private final RestaurantRatingJpaRepository restaurantRatingJpaRepository;

    RestaurantRatingDbImpl(RestaurantRatingJpaRepository restaurantRatingJpaRepository) {
        this.restaurantRatingJpaRepository = restaurantRatingJpaRepository;
    }

    @Override
    public RestaurantRatingEntity save(RestaurantRatingEntity restaurantRatingEntity) {
        Assert.notNull(restaurantRatingEntity, "restaurantRatingEntity must not be null");
        log.debug("Saving restaurant rating for restaurant id {}", restaurantRatingEntity.getRestaurant().getId());
        RestaurantRatingEntity saved = restaurantRatingJpaRepository.save(restaurantRatingEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<RestaurantRatingEntity> findByRestaurantIdAndUserId(Long restaurantId, Long userId) {
        Assert.notNull(restaurantId, "restaurantId must not be null");
        Assert.notNull(userId, "userId must not be null");
        log.debug("Finding restaurant rating for restaurant id {} and user id {}", restaurantId, userId);
        return restaurantRatingJpaRepository.findByRestaurantIdAndUserId(restaurantId, userId);
    }

    @Override
    public List<RestaurantRatingEntity> findByRestaurantId(Long restaurantId) {
        Assert.notNull(restaurantId, "restaurantId must not be null");
        log.debug("Finding restaurant ratings for restaurant id {}", restaurantId);
        return restaurantRatingJpaRepository.findByRestaurantId(restaurantId);
    }
}
