package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.FoodItemConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodItemEntityTest {

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

    @Test
    void given_validFields_when_construct_then_fieldsArePopulated() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");

        assertThat(entity.getRestaurant()).isEqualTo(restaurant);
        assertThat(entity.getName()).isEqualTo("Ribeye Steak");
        assertThat(entity.getDishCategory()).isEqualTo("Steak");
    }

    @Test
    void given_maxLengthName_when_construct_then_accepted() {
        String maxLengthName = "R".repeat(FoodItemConstants.NAME_MAX_LENGTH);

        FoodItemEntity entity = new FoodItemEntity(restaurant, maxLengthName, "Steak");

        assertThat(entity.getName()).hasSize(FoodItemConstants.NAME_MAX_LENGTH);
    }

    @Test
    void given_nullRestaurant_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemEntity(null, "Ribeye Steak", "Steak"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankName_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemEntity(restaurant, "   ", "Steak"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankDishCategory_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemEntity(restaurant, "Ribeye Steak", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullDishCategory_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new FoodItemEntity(restaurant, "Ribeye Steak", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
