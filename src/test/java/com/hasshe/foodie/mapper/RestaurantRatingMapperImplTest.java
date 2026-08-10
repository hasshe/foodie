package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantRatingMapperImplTest {

    private final RestaurantRatingMapperImpl restaurantRatingMapperImpl = new RestaurantRatingMapperImpl();

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 40);

        RestaurantRatingDomain domain = restaurantRatingMapperImpl.mapToDomain(entity);

        assertThat(domain.raterUsername()).isEqualTo("chef123");
        assertThat(domain.raterDisplayName()).isEqualTo("Chef");
        assertThat(domain.employeesService()).isEqualTo(80);
        assertThat(domain.audioMusic()).isEqualTo(70);
        assertThat(domain.generalVibes()).isEqualTo(90);
        assertThat(domain.priceForQuality()).isEqualTo(60);
        assertThat(domain.locationLocale()).isEqualTo(50);
        assertThat(domain.foodQuality()).isEqualTo(40);
    }

    @Test
    void given_anotherValidEntity_when_mapToDomain_then_averageScoreIsComputedCorrectly() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 100, 100, 100, 100, 100, 100);

        RestaurantRatingDomain domain = restaurantRatingMapperImpl.mapToDomain(entity);

        assertThat(domain.averageScore()).isEqualTo(100.0);
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        RestaurantRatingDomain domain = new RestaurantRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, 50, 40, LocalDateTime.now(), LocalDateTime.now()
        );

        RestaurantRatingDisplay display = restaurantRatingMapperImpl.mapToDisplay(domain);

        assertThat(display.raterDisplayName()).isEqualTo("Chef");
        assertThat(display.employeesService()).isEqualTo(80);
        assertThat(display.foodQuality()).isEqualTo(40);
        assertThat(display.averageScore()).isEqualTo(domain.averageScore());
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        RestaurantRatingDomain domain = new RestaurantRatingDomain(
                2L, 1L, "foodie99", "Foodie", 10, 20, 30, 40, 50, 60, LocalDateTime.now(), LocalDateTime.now()
        );

        RestaurantRatingDisplay display = restaurantRatingMapperImpl.mapToDisplay(domain);

        assertThat(display.raterDisplayName()).isEqualTo("Foodie");
        assertThat(display.locationLocale()).isEqualTo(50);
        assertThat(display.foodQuality()).isEqualTo(60);
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_multipleRatings_when_mapToSummaryDomain_then_computesAveragesAndCount() {
        UserEntity secondRater = new UserEntity("foodie99", "hashedPassword", "Foodie");
        RestaurantRatingEntity first = new RestaurantRatingEntity(restaurant, rater, 100, 100, 100, 100, 100, 100);
        RestaurantRatingEntity second = new RestaurantRatingEntity(restaurant, secondRater, 50, 50, 50, 50, 50, 50);

        RestaurantRatingSummaryDomain summary = restaurantRatingMapperImpl.mapToSummaryDomain(restaurant, List.of(first, second));

        assertThat(summary.ratingCount()).isEqualTo(2);
        assertThat(summary.averageEmployeesService()).isEqualTo(75.0);
        assertThat(summary.averageFoodQuality()).isEqualTo(75.0);
        assertThat(summary.overallAverage()).isEqualTo(75.0);
        assertThat(summary.restaurantName()).isEqualTo("The Diner");
    }

    @Test
    void given_noRatings_when_mapToSummaryDomain_then_averagesAreZeroAndCountIsZero() {
        RestaurantRatingSummaryDomain summary = restaurantRatingMapperImpl.mapToSummaryDomain(restaurant, List.of());

        assertThat(summary.ratingCount()).isZero();
        assertThat(summary.overallAverage()).isZero();
        assertThat(summary.ratings()).isEmpty();
    }

    @Test
    void given_nullRestaurantEntity_when_mapToSummaryDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToSummaryDomain(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRatingEntities_when_mapToSummaryDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToSummaryDomain(restaurant, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_requestingUserHasRated_when_mapToSummaryDisplay_then_currentUserRatingIsPopulated() {
        RestaurantRatingDomain ownRating = new RestaurantRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, 50, 40, LocalDateTime.now(), LocalDateTime.now()
        );
        RestaurantRatingSummaryDomain summaryDomain = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 80, 70, 90, 60, 50, 40, 65, 1, List.of(ownRating)
        );

        RestaurantRatingSummaryDisplay display = restaurantRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "chef123");

        assertThat(display.currentUserRating()).isNotNull();
        assertThat(display.currentUserRating().employeesService()).isEqualTo(80);
        assertThat(display.ratings()).hasSize(1);
    }

    @Test
    void given_requestingUserHasNotRated_when_mapToSummaryDisplay_then_currentUserRatingIsNull() {
        RestaurantRatingDomain otherRating = new RestaurantRatingDomain(
                1L, 1L, "foodie99", "Foodie", 80, 70, 90, 60, 50, 40, LocalDateTime.now(), LocalDateTime.now()
        );
        RestaurantRatingSummaryDomain summaryDomain = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 80, 70, 90, 60, 50, 40, 65, 1, List.of(otherRating)
        );

        RestaurantRatingSummaryDisplay display = restaurantRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "chef123");

        assertThat(display.currentUserRating()).isNull();
    }

    @Test
    void given_nullSummaryDomain_when_mapToSummaryDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToSummaryDisplay(null, "chef123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankRequestingUsername_when_mapToSummaryDisplay_then_throwsIllegalArgumentException() {
        RestaurantRatingSummaryDomain summaryDomain = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 0, 0, 0, 0, 0, 0, 0, 0, List.of()
        );

        assertThatThrownBy(() -> restaurantRatingMapperImpl.mapToSummaryDisplay(summaryDomain, "  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
