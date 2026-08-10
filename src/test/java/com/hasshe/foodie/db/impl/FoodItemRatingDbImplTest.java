package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
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
class FoodItemRatingDbImplTest {

    @Mock
    private FoodItemRatingJpaRepository foodItemRatingJpaRepository;

    @InjectMocks
    private FoodItemRatingDbImpl foodItemRatingDbImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final FoodItemEntity foodItem = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);
        when(foodItemRatingJpaRepository.save(entity)).thenReturn(entity);

        FoodItemRatingEntity result = foodItemRatingDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 10);
        when(foodItemRatingJpaRepository.save(entity)).thenReturn(entity);

        foodItemRatingDbImpl.save(entity);

        verify(foodItemRatingJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingRatingForFoodItemAndUser_when_findByFoodItemIdAndUserId_then_returnsRating() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);
        when(foodItemRatingJpaRepository.findByFoodItemIdAndUserId(1L, 2L)).thenReturn(Optional.of(entity));

        Optional<FoodItemRatingEntity> result = foodItemRatingDbImpl.findByFoodItemIdAndUserId(1L, 2L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_noRatingForFoodItemAndUser_when_findByFoodItemIdAndUserId_then_returnsEmptyOptional() {
        when(foodItemRatingJpaRepository.findByFoodItemIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        Optional<FoodItemRatingEntity> result = foodItemRatingDbImpl.findByFoodItemIdAndUserId(1L, 99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullFoodItemId_when_findByFoodItemIdAndUserId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingDbImpl.findByFoodItemIdAndUserId(null, 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullUserId_when_findByFoodItemIdAndUserId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingDbImpl.findByFoodItemIdAndUserId(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_ratingsForFoodItem_when_findByFoodItemId_then_returnsRatings() {
        FoodItemRatingEntity entity = new FoodItemRatingEntity(foodItem, rater, 80);
        when(foodItemRatingJpaRepository.findByFoodItemId(1L)).thenReturn(List.of(entity));

        List<FoodItemRatingEntity> result = foodItemRatingDbImpl.findByFoodItemId(1L);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_noRatingsForFoodItem_when_findByFoodItemId_then_returnsEmptyList() {
        when(foodItemRatingJpaRepository.findByFoodItemId(1L)).thenReturn(List.of());

        List<FoodItemRatingEntity> result = foodItemRatingDbImpl.findByFoodItemId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullFoodItemId_when_findByFoodItemId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingDbImpl.findByFoodItemId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
