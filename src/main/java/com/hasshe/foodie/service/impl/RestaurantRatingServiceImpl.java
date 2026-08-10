package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.RestaurantRatingDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantRatingMapper;
import com.hasshe.foodie.service.api.RestaurantRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class RestaurantRatingServiceImpl implements RestaurantRatingService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantRatingServiceImpl.class);

    private final RestaurantRatingDb restaurantRatingDb;
    private final RestaurantDb restaurantDb;
    private final GroupDb groupDb;
    private final UserDb userDb;
    private final RestaurantRatingMapper restaurantRatingMapper;

    RestaurantRatingServiceImpl(
            RestaurantRatingDb restaurantRatingDb,
            RestaurantDb restaurantDb,
            GroupDb groupDb,
            UserDb userDb,
            RestaurantRatingMapper restaurantRatingMapper
    ) {
        this.restaurantRatingDb = restaurantRatingDb;
        this.restaurantDb = restaurantDb;
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.restaurantRatingMapper = restaurantRatingMapper;
    }

    @Override
    public RestaurantRatingDomain rateRestaurant(String username, Long restaurantId, RateRestaurantDisplay rateRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        Assert.notNull(rateRestaurantDisplay, "rateRestaurantDisplay must not be null");
        log.debug("Rating restaurant id {} by username {}", restaurantId, username);

        UserEntity rater = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        RestaurantEntity restaurant = restaurantDb.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("No restaurant found with id: " + restaurantId));

        if (!groupDb.isMember(restaurant.getGroup().getId(), rater.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to rate it");
        }

        RestaurantRatingEntity ratingEntity = restaurantRatingDb.findByRestaurantIdAndUserId(restaurantId, rater.getId())
                .map(existing -> {
                    existing.updateScores(
                            rateRestaurantDisplay.employeesService(),
                            rateRestaurantDisplay.audioMusic(),
                            rateRestaurantDisplay.generalVibes(),
                            rateRestaurantDisplay.priceForQuality(),
                            rateRestaurantDisplay.locationLocale(),
                            rateRestaurantDisplay.foodQuality()
                    );
                    return existing;
                })
                .orElseGet(() -> new RestaurantRatingEntity(
                        restaurant,
                        rater,
                        rateRestaurantDisplay.employeesService(),
                        rateRestaurantDisplay.audioMusic(),
                        rateRestaurantDisplay.generalVibes(),
                        rateRestaurantDisplay.priceForQuality(),
                        rateRestaurantDisplay.locationLocale(),
                        rateRestaurantDisplay.foodQuality()
                ));

        RestaurantRatingEntity savedRatingEntity = restaurantRatingDb.save(ratingEntity);

        RestaurantRatingDomain restaurantRatingDomain = restaurantRatingMapper.mapToDomain(savedRatingEntity);
        assert restaurantRatingDomain != null : "mapper must never return null";
        log.info("Saved rating for restaurant id {} by username {}", restaurantId, username);
        return restaurantRatingDomain;
    }

    @Override
    public RestaurantRatingSummaryDomain getRatingSummary(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        log.debug("Getting rating summary for restaurant id {} requested by username {}", restaurantId, username);

        UserEntity requester = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        RestaurantEntity restaurant = restaurantDb.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("No restaurant found with id: " + restaurantId));

        if (!groupDb.isMember(restaurant.getGroup().getId(), requester.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to view its ratings");
        }

        List<RestaurantRatingEntity> ratingEntities = restaurantRatingDb.findByRestaurantId(restaurantId);
        RestaurantRatingSummaryDomain restaurantRatingSummaryDomain = restaurantRatingMapper.mapToSummaryDomain(restaurant, ratingEntities);
        assert restaurantRatingSummaryDomain != null : "mapper must never return null";
        return restaurantRatingSummaryDomain;
    }
}
