package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.RestaurantRatingDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class RestaurantServiceImpl implements RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantDb restaurantDb;
    private final RestaurantRatingDb restaurantRatingDb;
    private final GroupDb groupDb;
    private final UserDb userDb;
    private final RestaurantMapper restaurantMapper;

    RestaurantServiceImpl(
            RestaurantDb restaurantDb,
            RestaurantRatingDb restaurantRatingDb,
            GroupDb groupDb,
            UserDb userDb,
            RestaurantMapper restaurantMapper
    ) {
        this.restaurantDb = restaurantDb;
        this.restaurantRatingDb = restaurantRatingDb;
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public RestaurantDomain addRestaurant(String username, AddRestaurantDisplay addRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(addRestaurantDisplay, "addRestaurantDisplay must not be null");
        log.debug("Adding restaurant '{}' for username {}", addRestaurantDisplay.name(), username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        GroupEntity groupEntity = groupDb.findById(addRestaurantDisplay.groupId())
                .orElseThrow(() -> new NotFoundException("No group found with id: " + addRestaurantDisplay.groupId()));

        if (!groupDb.isMember(addRestaurantDisplay.groupId(), userEntity.getId())) {
            throw new ValidationException("You must be a member of the group to add a restaurant to it");
        }

        RestaurantEntity restaurantEntity = new RestaurantEntity(
                addRestaurantDisplay.name(),
                addRestaurantDisplay.address(),
                groupEntity,
                addRestaurantDisplay.cuisineType(),
                addRestaurantDisplay.website(),
                addRestaurantDisplay.phone()
        );
        RestaurantEntity savedRestaurantEntity = restaurantDb.save(restaurantEntity);

        RestaurantDomain restaurantDomain = restaurantMapper.mapToDomain(savedRestaurantEntity, 0.0, 0);
        assert restaurantDomain != null : "mapper must never return null";
        log.info("Added restaurant '{}' with id {} to group {}", restaurantDomain.name(), restaurantDomain.id(), groupEntity.getId());
        return restaurantDomain;
    }

    @Override
    public List<RestaurantDomain> listRestaurantsForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Listing restaurants for username {}", username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        List<Long> groupIds = groupDb.findByMemberId(userEntity.getId()).stream().map(GroupEntity::getId).toList();
        if (groupIds.isEmpty()) {
            return List.of();
        }
        return restaurantDb.findByGroupIdInAndWishlist(groupIds, false).stream().map(this::mapWithAverageRating).toList();
    }

    private RestaurantDomain mapWithAverageRating(RestaurantEntity restaurantEntity) {
        List<RestaurantRatingEntity> ratingEntities = restaurantRatingDb.findByRestaurantId(restaurantEntity.getId());
        double averageRating = averageOf(ratingEntities);
        return restaurantMapper.mapToDomain(restaurantEntity, averageRating, ratingEntities.size());
    }

    private double averageOf(List<RestaurantRatingEntity> ratingEntities) {
        if (ratingEntities.isEmpty()) {
            return 0.0;
        }
        return ratingEntities.stream()
                .mapToDouble(rating -> (rating.getFood() + rating.getService() + rating.getVibe()) / 3.0)
                .average()
                .orElse(0.0);
    }
}
