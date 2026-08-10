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
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90);

        assertThat(entity.getRestaurant()).isEqualTo(restaurant);
        assertThat(entity.getRater()).isEqualTo(rater);
        assertThat(entity.getFood()).isEqualTo(80);
        assertThat(entity.getService()).isEqualTo(70);
        assertThat(entity.getVibe()).isEqualTo(90);
    }

    @Test
    void given_boundaryScores_when_construct_then_accepted() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 1, 100, 1);

        assertThat(entity.getFood()).isEqualTo(1);
        assertThat(entity.getService()).isEqualTo(100);
    }

    @Test
    void given_nullRestaurant_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(null, rater, 80, 70, 90))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRater_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, null, 80, 70, 90))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreBelowMinimum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, rater, 0, 70, 90))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreAboveMaximum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantRatingEntity(restaurant, rater, 80, 101, 90))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_newScores_when_updateScores_then_fieldsAreUpdated() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90);

        entity.updateScores(10, 20, 30);

        assertThat(entity.getFood()).isEqualTo(10);
        assertThat(entity.getService()).isEqualTo(20);
        assertThat(entity.getVibe()).isEqualTo(30);
    }

    @Test
    void given_invalidScore_when_updateScores_then_throwsIllegalArgumentExceptionAndKeepsOldValues() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90);

        assertThatThrownBy(() -> entity.updateScores(80, 70, 200))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entity.getVibe()).isEqualTo(90);
    }
}
