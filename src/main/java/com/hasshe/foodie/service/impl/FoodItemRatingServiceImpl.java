package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemRatingMapper;
import com.hasshe.foodie.service.api.FoodItemRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class FoodItemRatingServiceImpl implements FoodItemRatingService {

    private static final Logger log = LoggerFactory.getLogger(FoodItemRatingServiceImpl.class);

    private final FoodItemRatingDb foodItemRatingDb;
    private final FoodItemDb foodItemDb;
    private final GroupDb groupDb;
    private final UserDb userDb;
    private final FoodItemRatingMapper foodItemRatingMapper;

    FoodItemRatingServiceImpl(
            FoodItemRatingDb foodItemRatingDb,
            FoodItemDb foodItemDb,
            GroupDb groupDb,
            UserDb userDb,
            FoodItemRatingMapper foodItemRatingMapper
    ) {
        this.foodItemRatingDb = foodItemRatingDb;
        this.foodItemDb = foodItemDb;
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.foodItemRatingMapper = foodItemRatingMapper;
    }

    @Override
    public FoodItemRatingDomain rateFoodItem(String username, Long foodItemId, RateFoodItemDisplay rateFoodItemDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(foodItemId, "foodItemId must not be null");
        Assert.notNull(rateFoodItemDisplay, "rateFoodItemDisplay must not be null");
        log.debug("Rating food item id {} by username {}", foodItemId, username);

        UserEntity rater = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        FoodItemEntity foodItem = foodItemDb.findById(foodItemId)
                .orElseThrow(() -> new NotFoundException("No food item found with id: " + foodItemId));

        if (!groupDb.isMember(foodItem.getRestaurant().getGroup().getId(), rater.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to rate this food item");
        }

        FoodItemRatingEntity ratingEntity = foodItemRatingDb.findByFoodItemIdAndUserId(foodItemId, rater.getId())
                .map(existing -> {
                    existing.updateScores(
                            rateFoodItemDisplay.taste(),
                            rateFoodItemDisplay.presentation(),
                            rateFoodItemDisplay.portionQuality(),
                            rateFoodItemDisplay.valueForPrice()
                    );
                    return existing;
                })
                .orElseGet(() -> new FoodItemRatingEntity(
                        foodItem,
                        rater,
                        rateFoodItemDisplay.taste(),
                        rateFoodItemDisplay.presentation(),
                        rateFoodItemDisplay.portionQuality(),
                        rateFoodItemDisplay.valueForPrice()
                ));

        FoodItemRatingEntity savedRatingEntity = foodItemRatingDb.save(ratingEntity);

        FoodItemRatingDomain foodItemRatingDomain = foodItemRatingMapper.mapToDomain(savedRatingEntity);
        assert foodItemRatingDomain != null : "mapper must never return null";
        log.info("Saved rating for food item id {} by username {}", foodItemId, username);
        return foodItemRatingDomain;
    }

    @Override
    public FoodItemRatingSummaryDomain getRatingSummary(String username, Long foodItemId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(foodItemId, "foodItemId must not be null");
        log.debug("Getting rating summary for food item id {} requested by username {}", foodItemId, username);

        UserEntity requester = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        FoodItemEntity foodItem = foodItemDb.findById(foodItemId)
                .orElseThrow(() -> new NotFoundException("No food item found with id: " + foodItemId));

        if (!groupDb.isMember(foodItem.getRestaurant().getGroup().getId(), requester.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to view this food item's ratings");
        }

        List<FoodItemRatingEntity> ratingEntities = foodItemRatingDb.findByFoodItemId(foodItemId);
        FoodItemRatingSummaryDomain foodItemRatingSummaryDomain = foodItemRatingMapper.mapToSummaryDomain(foodItem, ratingEntities);
        assert foodItemRatingSummaryDomain != null : "mapper must never return null";
        return foodItemRatingSummaryDomain;
    }
}
