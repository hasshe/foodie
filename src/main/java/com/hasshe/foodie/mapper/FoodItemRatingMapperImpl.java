package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
class FoodItemRatingMapperImpl implements FoodItemRatingMapper {

    @Override
    public FoodItemRatingDomain mapToDomain(FoodItemRatingEntity foodItemRatingEntity) {
        Assert.notNull(foodItemRatingEntity, "foodItemRatingEntity must not be null");
        FoodItemRatingDomain foodItemRatingDomain = new FoodItemRatingDomain(
                foodItemRatingEntity.getId(),
                foodItemRatingEntity.getFoodItem().getId(),
                foodItemRatingEntity.getRater().getUsername(),
                foodItemRatingEntity.getRater().getDisplayName(),
                foodItemRatingEntity.getRating(),
                foodItemRatingEntity.getCreatedAt(),
                foodItemRatingEntity.getUpdatedAt()
        );
        assert foodItemRatingDomain != null : "mapping must never produce null";
        return foodItemRatingDomain;
    }

    @Override
    public FoodItemRatingDisplay mapToDisplay(FoodItemRatingDomain foodItemRatingDomain) {
        Assert.notNull(foodItemRatingDomain, "foodItemRatingDomain must not be null");
        FoodItemRatingDisplay foodItemRatingDisplay = new FoodItemRatingDisplay(
                foodItemRatingDomain.id(),
                foodItemRatingDomain.raterDisplayName(),
                foodItemRatingDomain.rating()
        );
        assert foodItemRatingDisplay != null : "mapping must never produce null";
        return foodItemRatingDisplay;
    }

    @Override
    public FoodItemRatingSummaryDomain mapToSummaryDomain(FoodItemEntity foodItemEntity, List<FoodItemRatingEntity> foodItemRatingEntities) {
        Assert.notNull(foodItemEntity, "foodItemEntity must not be null");
        Assert.notNull(foodItemRatingEntities, "foodItemRatingEntities must not be null");

        List<FoodItemRatingDomain> ratingDomains = foodItemRatingEntities.stream().map(this::mapToDomain).toList();
        int ratingCount = ratingDomains.size();
        double averageRating = ratingCount == 0
                ? 0.0
                : ratingDomains.stream().mapToInt(FoodItemRatingDomain::rating).average().orElse(0.0);

        FoodItemRatingSummaryDomain foodItemRatingSummaryDomain = new FoodItemRatingSummaryDomain(
                foodItemEntity.getId(),
                foodItemEntity.getName(),
                averageRating,
                ratingCount,
                ratingDomains
        );
        assert foodItemRatingSummaryDomain != null : "mapping must never produce null";
        return foodItemRatingSummaryDomain;
    }

    @Override
    public FoodItemRatingSummaryDisplay mapToSummaryDisplay(FoodItemRatingSummaryDomain foodItemRatingSummaryDomain, String requestingUsername) {
        Assert.notNull(foodItemRatingSummaryDomain, "foodItemRatingSummaryDomain must not be null");
        Assert.hasText(requestingUsername, "requestingUsername must not be blank");

        List<FoodItemRatingDisplay> ratingDisplays = foodItemRatingSummaryDomain.ratings().stream().map(this::mapToDisplay).toList();
        FoodItemRatingDisplay currentUserRating = foodItemRatingSummaryDomain.ratings().stream()
                .filter(rating -> rating.raterUsername().equals(requestingUsername))
                .findFirst()
                .map(this::mapToDisplay)
                .orElse(null);

        FoodItemRatingSummaryDisplay foodItemRatingSummaryDisplay = new FoodItemRatingSummaryDisplay(
                foodItemRatingSummaryDomain.foodItemId(),
                foodItemRatingSummaryDomain.foodItemName(),
                foodItemRatingSummaryDomain.averageRating(),
                foodItemRatingSummaryDomain.ratingCount(),
                ratingDisplays,
                currentUserRating
        );
        assert foodItemRatingSummaryDisplay != null : "mapping must never produce null";
        return foodItemRatingSummaryDisplay;
    }
}
