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
    void given_validScores_when_construct_then_fieldsArePopulated() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);

        assertThat(entity.getFoodItem()).isEqualTo(foodItem);
        assertThat(entity.getRater()).isEqualTo(rater);
        assertThat(entity.getTaste()).isEqualTo(80);
        assertThat(entity.getPresentation()).isEqualTo(70);
        assertThat(entity.getPortionQuality()).isEqualTo(90);
        assertThat(entity.getValueForPrice()).isEqualTo(60);
    }

    @Test
    void given_boundaryScores_when_construct_then_accepted() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 1, 100, 1, 100);

        assertThat(entity.getTaste()).isEqualTo(1);
        assertThat(entity.getPresentation()).isEqualTo(100);
    }

    @Test
    void given_nullFoodItem_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(null, rater, 80, 70, 90, 60))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRater_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, null, 80, 70, 90, 60))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreBelowMinimum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, rater, 0, 70, 90, 60))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_scoreAboveMaximum_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemRatingEntity(foodItem, rater, 80, 101, 90, 60))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_newScores_when_updateScores_then_fieldsAreUpdated() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);

        entity.updateScores(10, 20, 30, 40);

        assertThat(entity.getTaste()).isEqualTo(10);
        assertThat(entity.getPresentation()).isEqualTo(20);
        assertThat(entity.getPortionQuality()).isEqualTo(30);
        assertThat(entity.getValueForPrice()).isEqualTo(40);
    }

    @Test
    void given_invalidScore_when_updateScores_then_throwsIllegalArgumentExceptionAndKeepsOldValues() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);

        assertThatThrownBy(() -> entity.updateScores(80, 70, 90, 200))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entity.getValueForPrice()).isEqualTo(60);
    }
}
