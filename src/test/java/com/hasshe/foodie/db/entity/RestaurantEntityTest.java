package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.RestaurantConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantEntityTest {

    private final GroupEntity group = new GroupEntity("Foodies");

    @Test
    void given_validRequiredFields_when_construct_then_fieldsArePopulated() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

        assertThat(entity.getName()).isEqualTo("The Diner");
        assertThat(entity.getAddress()).isEqualTo("123 Main St");
        assertThat(entity.getGroup()).isEqualTo(group);
    }

    @Test
    void given_allFieldsProvided_when_construct_then_optionalFieldsArePopulated() {
        RestaurantEntity entity = new RestaurantEntity(
                "The Diner", "123 Main St", group, "American", "https://diner.example", "555-1234"
        );

        assertThat(entity.getCuisineType()).contains("American");
        assertThat(entity.getWebsite()).contains("https://diner.example");
        assertThat(entity.getPhone()).contains("555-1234");
    }

    @Test
    void given_blankName_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantEntity("   ", "123 Main St", group, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankAddress_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantEntity("The Diner", "   ", group, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullGroup_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new RestaurantEntity("The Diner", "123 Main St", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthName_when_construct_then_accepted() {
        String maxLengthName = "R".repeat(RestaurantConstants.NAME_MAX_LENGTH);

        RestaurantEntity entity = new RestaurantEntity(maxLengthName, "123 Main St", group, null, null, null);

        assertThat(entity.getName()).hasSize(RestaurantConstants.NAME_MAX_LENGTH);
    }

    @Test
    void given_noOptionalFields_when_construct_then_optionalGettersReturnEmpty() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

        assertThat(entity.getCuisineType()).isEmpty();
        assertThat(entity.getWebsite()).isEmpty();
        assertThat(entity.getPhone()).isEmpty();
    }
}
