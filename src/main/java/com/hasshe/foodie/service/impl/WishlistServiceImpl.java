package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class WishlistServiceImpl implements WishlistService {

    private static final Logger log = LoggerFactory.getLogger(WishlistServiceImpl.class);

    private final RestaurantDb restaurantDb;
    private final GroupDb groupDb;
    private final UserDb userDb;
    private final RestaurantMapper restaurantMapper;

    WishlistServiceImpl(RestaurantDb restaurantDb, GroupDb groupDb, UserDb userDb, RestaurantMapper restaurantMapper) {
        this.restaurantDb = restaurantDb;
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public RestaurantDomain addToWishlist(String username, AddRestaurantDisplay addRestaurantDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(addRestaurantDisplay, "addRestaurantDisplay must not be null");
        log.debug("Adding restaurant '{}' to wishlist for username {}", addRestaurantDisplay.name(), username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        GroupEntity groupEntity = groupDb.findById(addRestaurantDisplay.groupId())
                .orElseThrow(() -> new NotFoundException("No group found with id: " + addRestaurantDisplay.groupId()));

        if (!groupDb.isMember(addRestaurantDisplay.groupId(), userEntity.getId())) {
            throw new ValidationException("You must be a member of the group to add a restaurant to its wishlist");
        }

        RestaurantEntity restaurantEntity = new RestaurantEntity(
                addRestaurantDisplay.name(),
                addRestaurantDisplay.address(),
                groupEntity,
                addRestaurantDisplay.cuisineType(),
                addRestaurantDisplay.website(),
                addRestaurantDisplay.phone(),
                true
        );
        RestaurantEntity savedRestaurantEntity = restaurantDb.save(restaurantEntity);

        RestaurantDomain restaurantDomain = restaurantMapper.mapToDomain(savedRestaurantEntity, 0.0, 0);
        assert restaurantDomain != null : "mapper must never return null";
        log.info("Added restaurant '{}' with id {} to wishlist for group {}", restaurantDomain.name(), restaurantDomain.id(), groupEntity.getId());
        return restaurantDomain;
    }

    @Override
    public List<RestaurantDomain> listWishlistForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Listing wishlist for username {}", username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        List<Long> groupIds = groupDb.findByMemberId(userEntity.getId()).stream().map(GroupEntity::getId).toList();
        if (groupIds.isEmpty()) {
            return List.of();
        }
        return restaurantDb.findByGroupIdInAndWishlist(groupIds, true).stream()
                .map(restaurantEntity -> restaurantMapper.mapToDomain(restaurantEntity, 0.0, 0))
                .toList();
    }

    @Override
    public RestaurantDomain checkOffWishlistItem(String username, Long restaurantId) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(restaurantId, "restaurantId must not be null");
        log.debug("Checking off wishlist item id {} for username {}", restaurantId, username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));
        RestaurantEntity restaurantEntity = restaurantDb.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("No restaurant found with id: " + restaurantId));

        if (!groupDb.isMember(restaurantEntity.getGroup().getId(), userEntity.getId())) {
            throw new ValidationException("You must be a member of the restaurant's group to check it off the wishlist");
        }

        restaurantEntity.markVisited();
        RestaurantEntity savedRestaurantEntity = restaurantDb.save(restaurantEntity);

        RestaurantDomain restaurantDomain = restaurantMapper.mapToDomain(savedRestaurantEntity, 0.0, 0);
        assert restaurantDomain != null : "mapper must never return null";
        log.info("Checked off restaurant '{}' with id {} from wishlist", restaurantDomain.name(), restaurantDomain.id());
        return restaurantDomain;
    }
}
