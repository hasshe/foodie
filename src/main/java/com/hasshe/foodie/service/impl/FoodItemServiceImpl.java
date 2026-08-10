package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemCategoryGroupDomain;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.domain.FoodItemWithRestaurantDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemMapper;
import com.hasshe.foodie.service.api.FoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<FoodItemCategoryGroupDomain> listFoodItemsGroupedByCategory(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Listing food items grouped by category for username {}", username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        List<Long> groupIds = groupDb.findByMemberId(userEntity.getId()).stream().map(GroupEntity::getId).toList();
        if (groupIds.isEmpty()) {
            return List.of();
        }

        List<FoodItemWithRestaurantDomain> foodItemsWithRestaurant = restaurantDb.findByGroupIdInAndWishlist(groupIds, false).stream()
                .flatMap(restaurantEntity -> foodItemDb.findByRestaurantId(restaurantEntity.getId()).stream())
                .map(this::mapWithRestaurantAndAverageRating)
                .toList();

        Map<String, List<FoodItemWithRestaurantDomain>> groupedByCategory = foodItemsWithRestaurant.stream()
                .collect(Collectors.groupingBy(FoodItemWithRestaurantDomain::dishCategory, LinkedHashMap::new, Collectors.toList()));

        return groupedByCategory.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new FoodItemCategoryGroupDomain(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparingDouble(FoodItemWithRestaurantDomain::averageRating).reversed())
                                .toList()
                ))
                .toList();
    }

    private FoodItemDomain mapWithAverageRating(FoodItemEntity foodItemEntity) {
        List<FoodItemRatingEntity> ratingEntities = foodItemRatingDb.findByFoodItemId(foodItemEntity.getId());
        double averageRating = averageOf(ratingEntities);
        return foodItemMapper.mapToDomain(foodItemEntity, averageRating, ratingEntities.size());
    }

    private FoodItemWithRestaurantDomain mapWithRestaurantAndAverageRating(FoodItemEntity foodItemEntity) {
        List<FoodItemRatingEntity> ratingEntities = foodItemRatingDb.findByFoodItemId(foodItemEntity.getId());
        double averageRating = averageOf(ratingEntities);
        return new FoodItemWithRestaurantDomain(
                foodItemEntity.getId(),
                foodItemEntity.getName(),
                foodItemEntity.getDishCategory(),
                foodItemEntity.getRestaurant().getName(),
                averageRating,
                ratingEntities.size()
        );
    }

    private double averageOf(List<FoodItemRatingEntity> ratingEntities) {
        if (ratingEntities.isEmpty()) {
            return 0.0;
        }
        return ratingEntities.stream()
                .mapToInt(FoodItemRatingEntity::getRating)
                .average()
                .orElse(0.0);
    }
}
