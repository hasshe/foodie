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

    @Test
    void given_sixArgConstructor_when_construct_then_isWishlistIsFalse() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

        assertThat(entity.isWishlist()).isFalse();
    }

    @Test
    void given_wishlistTrue_when_construct_then_isWishlistIsTrue() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);

        assertThat(entity.isWishlist()).isTrue();
    }

    @Test
    void given_wishlistFalseExplicit_when_construct_then_isWishlistIsFalse() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, false);

        assertThat(entity.isWishlist()).isFalse();
    }

    @Test
    void given_wishlistItem_when_markVisited_then_isWishlistBecomesFalse() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);

        entity.markVisited();

        assertThat(entity.isWishlist()).isFalse();
    }

    @Test
    void given_alreadyVisitedItem_when_markVisited_then_remainsFalse() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, false);

        entity.markVisited();

        assertThat(entity.isWishlist()).isFalse();
    }
}
