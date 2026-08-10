package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodItemRatingMapperImplTest {

    private final FoodItemRatingMapperImpl foodItemRatingMapperImpl = new FoodItemRatingMapperImpl();

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final FoodItemEntity foodItem = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);

        FoodItemRatingDomain domain = foodItemRatingMapperImpl.mapToDomain(entity);

        assertThat(domain.raterUsername()).isEqualTo("chef123");
        assertThat(domain.raterDisplayName()).isEqualTo("Chef");
        assertThat(domain.taste()).isEqualTo(80);
        assertThat(domain.presentation()).isEqualTo(70);
        assertThat(domain.portionQuality()).isEqualTo(90);
        assertThat(domain.valueForPrice()).isEqualTo(60);
    }

    @Test
    void given_anotherValidEntity_when_mapToDomain_then_averageScoreIsComputedCorrectly() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 100, 100, 100, 100);

        FoodItemRatingDomain domain = foodItemRatingMapperImpl.mapToDomain(entity);

        assertThat(domain.averageScore()).isEqualTo(100.0);
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemRatingDomain domain = new FoodItemRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );

        FoodItemRatingDisplay display = foodItemRatingMapperImpl.mapToDisplay(domain);

        assertThat(display.raterDisplayName()).isEqualTo("Chef");
        assertThat(display.taste()).isEqualTo(80);
        assertThat(display.averageScore()).isEqualTo(domain.averageScore());
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemRatingDomain domain = new FoodItemRatingDomain(
                2L, 1L, "foodie99", "Foodie", 10, 20, 30, 40, LocalDateTime.now(), LocalDateTime.now()
        );

        FoodItemRatingDisplay display = foodItemRatingMapperImpl.mapToDisplay(domain);

        assertThat(display.raterDisplayName()).isEqualTo("Foodie");
        assertThat(display.valueForPrice()).isEqualTo(40);
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_multipleRatings_when_mapToSummaryDomain_then_computesAveragesAndCount() {
        UserEntity secondRater = new UserEntity("foodie99", "hashedPassword", "Foodie");
        FoodItemRatingEntity first = new FoodItemRatingEntity(foodItem, rater, 100, 100, 100, 100);
        FoodItemRatingEntity second = new FoodItemRatingEntity(foodItem, secondRater, 50, 50, 50, 50);

        FoodItemRatingSummaryDomain summary = foodItemRatingMapperImpl.mapToSummaryDomain(foodItem, List.of(first, second));

        assertThat(summary.ratingCount()).isEqualTo(2);
        assertThat(summary.averageTaste()).isEqualTo(75.0);
        assertThat(summary.overallAverage()).isEqualTo(75.0);
        assertThat(summary.foodItemName()).isEqualTo("Ribeye Steak");
    }

    @Test
    void given_noRatings_when_mapToSummaryDomain_then_averagesAreZeroAndCountIsZero() {
        FoodItemRatingSummaryDomain summary = foodItemRatingMapperImpl.mapToSummaryDomain(foodItem, List.of());

        assertThat(summary.ratingCount()).isZero();
        assertThat(summary.overallAverage()).isZero();
        assertThat(summary.ratings()).isEmpty();
    }

    @Test
    void given_nullFoodItemEntity_when_mapToSummaryDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToSummaryDomain(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRatingEntities_when_mapToSummaryDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToSummaryDomain(foodItem, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_requestingUserHasRated_when_mapToSummaryDisplay_then_currentUserRatingIsPopulated() {
        FoodItemRatingDomain ownRating = new FoodItemRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );
        FoodItemRatingSummaryDomain summaryDomain = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 80, 70, 90, 60, 75, 1, List.of(ownRating)
        );

        FoodItemRatingSummaryDisplay display = foodItemRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "chef123");

        assertThat(display.currentUserRating()).isNotNull();
        assertThat(display.currentUserRating().taste()).isEqualTo(80);
        assertThat(display.ratings()).hasSize(1);
    }

    @Test
    void given_requestingUserHasNotRated_when_mapToSummaryDisplay_then_currentUserRatingIsNull() {
        FoodItemRatingDomain otherRating = new FoodItemRatingDomain(
                1L, 1L, "foodie99", "Foodie", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );
        FoodItemRatingSummaryDomain summaryDomain = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 80, 70, 90, 60, 75, 1, List.of(otherRating)
        );

        FoodItemRatingSummaryDisplay display = foodItemRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "chef123");

        assertThat(display.currentUserRating()).isNull();
    }

    @Test
    void given_nullSummaryDomain_when_mapToSummaryDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToSummaryDisplay(null, "chef123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankRequestingUsername_when_mapToSummaryDisplay_then_throwsIllegalArgumentException() {
        FoodItemRatingSummaryDomain summaryDomain = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 0, 0, 0, 0, 0, 0, List.of()
        );

        assertThatThrownBy(() -> foodItemRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
