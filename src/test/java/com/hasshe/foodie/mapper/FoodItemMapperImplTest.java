package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.domain.FoodItemCategoryGroupDomain;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.domain.FoodItemWithRestaurantDomain;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemWithRestaurantDisplay;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodItemMapperImplTest {

    private final FoodItemMapperImpl foodItemMapperImpl = new FoodItemMapperImpl();

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");

        FoodItemDomain domain = foodItemMapperImpl.mapToDomain(entity, 82.5, 4);

        assertThat(domain.name()).isEqualTo("Ribeye Steak");
        assertThat(domain.dishCategory()).isEqualTo("Steak");
        assertThat(domain.averageRating()).isEqualTo(82.5);
        assertThat(domain.ratingCount()).isEqualTo(4);
    }

    @Test
    void given_entityWithNoRatings_when_mapToDomain_then_returnsZeroAverageAndCount() {
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Caesar Salad", "Salad");

        FoodItemDomain domain = foodItemMapperImpl.mapToDomain(entity, 0.0, 0);

        assertThat(domain.name()).isEqualTo("Caesar Salad");
        assertThat(domain.dishCategory()).isEqualTo("Salad");
        assertThat(domain.averageRating()).isZero();
        assertThat(domain.ratingCount()).isZero();
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemMapperImpl.mapToDomain(null, 0.0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 82.5, 4, LocalDateTime.now(), LocalDateTime.now());

        FoodItemDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("Ribeye Steak");
        assertThat(display.dishCategory()).isEqualTo("Steak");
        assertThat(display.averageRating()).isEqualTo(82.5);
        assertThat(display.ratingCount()).isEqualTo(4);
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemDomain domain = new FoodItemDomain(2L, 1L, "Caesar Salad", "Salad", 0.0, 0, LocalDateTime.now(), LocalDateTime.now());

        FoodItemDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("Caesar Salad");
        assertThat(display.dishCategory()).isEqualTo("Salad");
        assertThat(display.ratingCount()).isZero();
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemMapperImpl.mapToDisplay((FoodItemDomain) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validFoodItemWithRestaurantDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemWithRestaurantDomain domain = new FoodItemWithRestaurantDomain(1L, "Ribeye Steak", "Steak", "The Diner", 82.5, 4);

        FoodItemWithRestaurantDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("Ribeye Steak");
        assertThat(display.restaurantName()).isEqualTo("The Diner");
        assertThat(display.averageRating()).isEqualTo(82.5);
        assertThat(display.ratingCount()).isEqualTo(4);
    }

    @Test
    void given_anotherValidFoodItemWithRestaurantDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        FoodItemWithRestaurantDomain domain = new FoodItemWithRestaurantDomain(2L, "Filet Mignon", "Steak", "Steakhouse", 0.0, 0);

        FoodItemWithRestaurantDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("Filet Mignon");
        assertThat(display.restaurantName()).isEqualTo("Steakhouse");
        assertThat(display.ratingCount()).isZero();
    }

    @Test
    void given_nullFoodItemWithRestaurantDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemMapperImpl.mapToDisplay((FoodItemWithRestaurantDomain) null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_categoryGroupWithMultipleItems_when_mapToDisplay_then_returnsMatchingDisplayWithMappedItems() {
        FoodItemWithRestaurantDomain first = new FoodItemWithRestaurantDomain(1L, "Ribeye Steak", "Steak", "The Diner", 82.5, 4);
        FoodItemWithRestaurantDomain second = new FoodItemWithRestaurantDomain(2L, "Filet Mignon", "Steak", "Steakhouse", 90.0, 2);
        FoodItemCategoryGroupDomain domain = new FoodItemCategoryGroupDomain("Steak", List.of(first, second));

        FoodItemCategoryGroupDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.dishCategory()).isEqualTo("Steak");
        assertThat(display.foodItems()).hasSize(2);
        assertThat(display.foodItems().get(0).name()).isEqualTo("Ribeye Steak");
        assertThat(display.foodItems().get(1).name()).isEqualTo("Filet Mignon");
    }

    @Test
    void given_categoryGroupWithEmptyItems_when_mapToDisplay_then_returnsDisplayWithEmptyList() {
        FoodItemCategoryGroupDomain domain = new FoodItemCategoryGroupDomain("Salad", List.of());

        FoodItemCategoryGroupDisplay display = foodItemMapperImpl.mapToDisplay(domain);

        assertThat(display.dishCategory()).isEqualTo("Salad");
        assertThat(display.foodItems()).isEmpty();
    }

    @Test
    void given_nullCategoryGroupDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemMapperImpl.mapToDisplay((FoodItemCategoryGroupDomain) null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
