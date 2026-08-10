package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodItemDbImplTest {

    @Mock
    private FoodItemJpaRepository foodItemJpaRepository;

    @InjectMocks
    private FoodItemDbImpl foodItemDbImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        when(foodItemJpaRepository.save(entity)).thenReturn(entity);

        FoodItemEntity result = foodItemDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Caesar Salad", "Salad");
        when(foodItemJpaRepository.save(entity)).thenReturn(entity);

        foodItemDbImpl.save(entity);

        verify(foodItemJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingId_when_findById_then_returnsEntity() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        when(foodItemJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<FoodItemEntity> result = foodItemDbImpl.findById(1L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownId_when_findById_then_returnsEmptyOptional() {
        when(foodItemJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<FoodItemEntity> result = foodItemDbImpl.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemDbImpl.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_foodItemsForRestaurant_when_findByRestaurantId_then_returnsFoodItems() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        when(foodItemJpaRepository.findByRestaurantId(1L)).thenReturn(List.of(entity));

        List<FoodItemEntity> result = foodItemDbImpl.findByRestaurantId(1L);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_noFoodItemsForRestaurant_when_findByRestaurantId_then_returnsEmptyList() {
        when(foodItemJpaRepository.findByRestaurantId(1L)).thenReturn(List.of());

        List<FoodItemEntity> result = foodItemDbImpl.findByRestaurantId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullRestaurantId_when_findByRestaurantId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemDbImpl.findByRestaurantId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
