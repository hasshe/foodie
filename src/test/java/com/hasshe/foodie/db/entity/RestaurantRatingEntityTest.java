package com.hasshe.foodie.db.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantRatingEntityTest {

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validScores_when_construct_then_fieldsArePopulated() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 40);

        assertThat(entity.getRestaurant()).isEqualTo(restaurant);
        assertThat(entity.getRater()).isEqualTo(rater);
        assertThat(entity.getEmployeesService()).isEqualTo(80);
        assertThat(entity.getAudioMusic()).isEqualTo(70);
        assertThat(entity.getGeneralVibes()).isEqualTo(90);
        assertThat(entity.getPriceForQuality()).isEqualTo(60);
        assertThat(entity.getLocationLocale()).isEqualTo(50);
        assertThat(entity.getFoodQuality()).isEqualTo(40);
    }

    @Test
    void given_boundaryScores_when_construct_then_accepted() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 1, 100, 1, 100, 1, 100);

        assertThat(entity.getEmployeesService()).isEqualTo(1);
        assertThat(entity.getAudioMusic()).isEqualTo(100);
        assertThat(entity.getFoodQuality()).isEqualTo(100);
    }

    @Test
    void given_nullRestaurant_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(null, rater, 80, 70, 90, 60, 50, 40))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRater_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, null, 80, 70, 90, 60, 50, 40))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreBelowMinimum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, rater, 0, 70, 90, 60, 50, 40))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreAboveMaximum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, rater, 80, 101, 90, 60, 50, 40))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_invalidFoodQuality_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_newScores_when_updateScores_then_fieldsAreUpdated() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 40);

        entity.updateScores(10, 20, 30, 40, 50, 60);

        assertThat(entity.getEmployeesService()).isEqualTo(10);
        assertThat(entity.getAudioMusic()).isEqualTo(20);
        assertThat(entity.getGeneralVibes()).isEqualTo(30);
        assertThat(entity.getPriceForQuality()).isEqualTo(40);
        assertThat(entity.getLocationLocale()).isEqualTo(50);
        assertThat(entity.getFoodQuality()).isEqualTo(60);
    }

    @Test
    void given_invalidScore_when_updateScores_then_throwsIllegalArgumentExceptionAndKeepsOldValues() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 40);

        assertThatThrownBy(() -> entity.updateScores(80, 70, 90, 60, 200, 40))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entity.getLocationLocale()).isEqualTo(50);
    }
}
