package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemMapper;
import com.hasshe.foodie.service.api.FoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class FoodItemServiceImpl implements FoodItemService {

    private static final Logger log = LoggerFactory.getLogger(FoodItemServiceImpl.class);

    private final FoodItemDb foodItemDb;
    private final FoodItemRatingDb foodItemRatingDb;
    private final RestaurantDb restaurantDb;
    private final GroupDb groupDb;
    private final UserDb userDb;
    private final FoodItemMapper foodItemMapper;

    FoodItemServiceImpl(
            FoodItemDb foodItemDb,
            FoodItemRatingDb foodItemRatingDb,
            RestaurantDb restaurantDb,
            GroupDb groupDb,
            UserDb userDb,
            FoodItemMapper foodItemMapper
    ) {
        this.foodItemDb = foodItemDb;
        this.foodItemRatingDb = foodItemRatingDb;
        this.restaurantDb = restaurantDb;
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.foodItemMapper = foodItemMapper;
    }

    @Override
    public FoodItemDomain addFoodItem(String username, Long restaurantId, AddFoodItemDisplay addFoodItemDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        Assert.notNull(addFoodItemDisplay, "addFoodItemDisplay must not be null");
        log.debug("Adding food item '{}' to restaurant id {} for username {}", addFoodItemDisplay.name(), restaurantId, username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        RestaurantEntity restaurantEntity = restaurantDb.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("No restaurant found with id: " + restaurantId));

        if (!groupDb.isMember(restaurantEntity.getGroup().getId(), userEntity.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to add a food item");
        }

        FoodItemEntity foodItemEntity = new FoodItemEntity(restaurantEntity, addFoodItemDisplay.name(), addFoodItemDisplay.dishCategory());
        FoodItemEntity savedFoodItemEntity = foodItemDb.save(foodItemEntity);

        FoodItemDomain foodItemDomain = foodItemMapper.mapToDomain(savedFoodItemEntity, 0.0, 0);
        assert foodItemDomain != null : "mapper must never return null";
        log.info("Added food item '{}' with id {} to restaurant id {}", foodItemDomain.name(), foodItemDomain.id(), restaurantId);
        return foodItemDomain;
    }

    @Override
    public List<FoodItemDomain> listFoodItemsForRestaurant(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        log.debug("Listing food items for restaurant id {} requested by username {}", restaurantId, username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        RestaurantEntity restaurantEntity = restaurantDb.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("No restaurant found with id: " + restaurantId));

        if (!groupDb.isMember(restaurantEntity.getGroup().getId(), userEntity.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to view its food items");
        }

        return foodItemDb.findByRestaurantId(restaurantId).stream()
                .map(this::mapWithAverageRating)
                .toList();
    }

    private FoodItemDomain mapWithAverageRating(FoodItemEntity foodItemEntity) {
        List<FoodItemRatingEntity> ratingEntities = foodItemRatingDb.findByFoodItemId(foodItemEntity.getId());
        double averageRating = averageOf(ratingEntities);
        return foodItemMapper.mapToDomain(foodItemEntity, averageRating, ratingEntities.size());
    }

    private double averageOf(List<FoodItemRatingEntity> ratingEntities) {
        if (ratingEntities.isEmpty()) {
            return 0.0;
        }
        return ratingEntities.stream()
                .mapToDouble(rating -> (rating.getTaste() + rating.getPresentation() + rating.getPortionQuality() + rating.getValueForPrice()) / 4.0)
                .average()
                .orElse(0.0);
    }
}
