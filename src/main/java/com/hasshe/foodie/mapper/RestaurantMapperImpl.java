package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.RestaurantDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class RestaurantMapperImpl implements RestaurantMapper {

    private final GroupMapper groupMapper;

    RestaurantMapperImpl(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Override
    public RestaurantDomain mapToDomain(RestaurantEntity restaurantEntity, double averageRating, int ratingCount) {
        Assert.notNull(restaurantEntity, "restaurantEntity must not be null");
        GroupDomain groupDomain = groupMapper.mapToDomain(restaurantEntity.getGroup());
        RestaurantDomain restaurantDomain = new RestaurantDomain(
                restaurantEntity.getId(),
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
                restaurantEntity.getCuisineType().orElse(null),
                restaurantEntity.getWebsite().orElse(null),
                restaurantEntity.getPhone().orElse(null),
                groupDomain,
                averageRating,
                ratingCount,
                restaurantEntity.getCreatedAt(),
                restaurantEntity.getUpdatedAt()
        );
        assert restaurantDomain != null : "mapping must never produce null";
        return restaurantDomain;
    }

    @Override
    public RestaurantDisplay mapToDisplay(RestaurantDomain restaurantDomain) {
        Assert.notNull(restaurantDomain, "restaurantDomain must not be null");
        RestaurantDisplay restaurantDisplay = new RestaurantDisplay(
                restaurantDomain.id(),
                restaurantDomain.name(),
                restaurantDomain.address(),
                restaurantDomain.cuisineType(),
                restaurantDomain.website(),
                restaurantDomain.phone(),
                restaurantDomain.group().name(),
                restaurantDomain.averageRating(),
                restaurantDomain.ratingCount()
        );
        assert restaurantDisplay != null : "mapping must never produce null";
        return restaurantDisplay;
    }
}
