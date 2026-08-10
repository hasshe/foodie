package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
class RestaurantRatingMapperImpl implements RestaurantRatingMapper {

    @Override
    public RestaurantRatingDomain mapToDomain(RestaurantRatingEntity restaurantRatingEntity) {
        Assert.notNull(restaurantRatingEntity, "restaurantRatingEntity must not be null");
        RestaurantRatingDomain restaurantRatingDomain = new RestaurantRatingDomain(
                restaurantRatingEntity.getId(),
                restaurantRatingEntity.getRestaurant().getId(),
                restaurantRatingEntity.getRater().getUsername(),
                restaurantRatingEntity.getRater().getDisplayName(),
                restaurantRatingEntity.getFood(),
                restaurantRatingEntity.getService(),
                restaurantRatingEntity.getVibe(),
                restaurantRatingEntity.getCreatedAt(),
                restaurantRatingEntity.getUpdatedAt()
        );
        assert restaurantRatingDomain != null : "mapping must never produce null";
        return restaurantRatingDomain;
    }

    @Override
    public RestaurantRatingDisplay mapToDisplay(RestaurantRatingDomain restaurantRatingDomain) {
        Assert.notNull(restaurantRatingDomain, "restaurantRatingDomain must not be null");
        RestaurantRatingDisplay restaurantRatingDisplay = new RestaurantRatingDisplay(
                restaurantRatingDomain.id(),
                restaurantRatingDomain.raterDisplayName(),
                restaurantRatingDomain.food(),
                restaurantRatingDomain.service(),
                restaurantRatingDomain.vibe(),
                restaurantRatingDomain.averageScore()
        );
        assert restaurantRatingDisplay != null : "mapping must never produce null";
        return restaurantRatingDisplay;
    }

    @Override
    public RestaurantRatingSummaryDomain mapToSummaryDomain(RestaurantEntity restaurantEntity, List<RestaurantRatingEntity> restaurantRatingEntities) {
        Assert.notNull(restaurantEntity, "restaurantEntity must not be null");
        Assert.notNull(restaurantRatingEntities, "restaurantRatingEntities must not be null");

        List<RestaurantRatingDomain> ratingDomains = restaurantRatingEntities.stream().map(this::mapToDomain).toList();
        int ratingCount = ratingDomains.size();
        double averageFood = average(ratingDomains, RestaurantRatingDomain::food);
        double averageService = average(ratingDomains, RestaurantRatingDomain::service);
        double averageVibe = average(ratingDomains, RestaurantRatingDomain::vibe);
        double overallAverage = ratingCount == 0
                ? 0.0
                : (averageFood + averageService + averageVibe) / 3.0;

        RestaurantRatingSummaryDomain restaurantRatingSummaryDomain = new RestaurantRatingSummaryDomain(
                restaurantEntity.getId(),
                restaurantEntity.getName(),
                averageFood,
                averageService,
                averageVibe,
                overallAverage,
                ratingCount,
                ratingDomains
        );
        assert restaurantRatingSummaryDomain != null : "mapping must never produce null";
        return restaurantRatingSummaryDomain;
    }

    @Override
    public RestaurantRatingSummaryDisplay mapToSummaryDisplay(RestaurantRatingSummaryDomain restaurantRatingSummaryDomain, String requestingUsername) {
        Assert.notNull(restaurantRatingSummaryDomain, "restaurantRatingSummaryDomain must not be null");
        Assert.hasText(requestingUsername, "requestingUsername must not be blank");

        List<RestaurantRatingDisplay> ratingDisplays = restaurantRatingSummaryDomain.ratings().stream().map(this::mapToDisplay).toList();
        RestaurantRatingDisplay currentUserRating = restaurantRatingSummaryDomain.ratings().stream()
                .filter(rating -> rating.raterUsername().equals(requestingUsername))
                .findFirst()
                .map(this::mapToDisplay)
                .orElse(null);

        RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay = new RestaurantRatingSummaryDisplay(
                restaurantRatingSummaryDomain.restaurantId(),
                restaurantRatingSummaryDomain.restaurantName(),
                restaurantRatingSummaryDomain.averageFood(),
                restaurantRatingSummaryDomain.averageService(),
                restaurantRatingSummaryDomain.averageVibe(),
                restaurantRatingSummaryDomain.overallAverage(),
                restaurantRatingSummaryDomain.ratingCount(),
                ratingDisplays,
                currentUserRating
        );
        assert restaurantRatingSummaryDisplay != null : "mapping must never produce null";
        return restaurantRatingSummaryDisplay;
    }

    private double average(List<RestaurantRatingDomain> ratingDomains, ToIntFunction<RestaurantRatingDomain> scoreExtractor) {
        if (ratingDomains.isEmpty()) {
            return 0.0;
        }
        return ratingDomains.stream().mapToInt(scoreExtractor).average().orElse(0.0);
    }
}
