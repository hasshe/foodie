package com.hasshe.foodie.db.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodItemRatingEntityTest {

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final FoodItemEntity foodItem = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validScore_when_construct_then_fieldsArePopulated() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);

        assertThat(entity.getFoodItem()).isEqualTo(foodItem);
        assertThat(entity.getRater()).isEqualTo(rater);
        assertThat(entity.getRating()).isEqualTo(80);
    }

    @Test
    void given_boundaryScores_when_construct_then_accepted() {
        FoodItemRatingEntity lowEntity = new FoodItemRatingEntity(foodItem, rater, 1);
        FoodItemRatingEntity highEntity = new FoodItemRatingEntity(foodItem, rater, 100);

        assertThat(lowEntity.getRating()).isEqualTo(1);
        assertThat(highEntity.getRating()).isEqualTo(100);
    }

    @Test
    void given_nullFoodItem_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(null, rater, 80))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRater_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, null, 80))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreBelowMinimum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, rater, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreAboveMaximum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, rater, 101))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_newScore_when_updateScore_then_fieldIsUpdated() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);

        entity.updateScore(40);

        assertThat(entity.getRating()).isEqualTo(40);
    }

    @Test
    void given_invalidScore_when_updateScore_then_throwsIllegalArgumentExceptionAndKeepsOldValue() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);

        assertThatThrownBy(() -> entity.updateScore(200))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entity.getRating()).isEqualTo(80);
    }
}
