package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.FoodItemDisplay;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        assertThatThrownBy(() -> foodItemMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
