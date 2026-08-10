package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class RestaurantDbImpl implements RestaurantDb {

    private static final Logger log = LoggerFactory.getLogger(RestaurantDbImpl.class);

    private final RestaurantJpaRepository restaurantJpaRepository;

    RestaurantDbImpl(RestaurantJpaRepository restaurantJpaRepository) {
        this.restaurantJpaRepository = restaurantJpaRepository;
    }

    @Override
    public RestaurantEntity save(RestaurantEntity restaurantEntity) {
        Assert.notNull(restaurantEntity, "restaurantEntity must not be null");
        log.debug("Saving restaurant with name {}", restaurantEntity.getName());
        RestaurantEntity saved = restaurantJpaRepository.save(restaurantEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<RestaurantEntity> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Finding restaurant by id {}", id);
        return restaurantJpaRepository.findById(id);
    }

    @Override
    public List<RestaurantEntity> findByGroupIdIn(List<Long> groupIds) {
        Assert.notNull(groupIds, "groupIds must not be null");
        log.debug("Finding restaurants for group ids {}", groupIds);
        return restaurantJpaRepository.findByGroupIdIn(groupIds);
    }
}
