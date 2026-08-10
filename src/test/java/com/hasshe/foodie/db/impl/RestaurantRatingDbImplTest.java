package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
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
class RestaurantRatingDbImplTest {

    @Mock
    private RestaurantRatingJpaRepository restaurantRatingJpaRepository;

    @InjectMocks
    private RestaurantRatingDbImpl restaurantRatingDbImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 45);
        when(restaurantRatingJpaRepository.save(entity)).thenReturn(entity);

        RestaurantRatingEntity result = restaurantRatingDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 10, 20, 30, 40, 50, 60);
        when(restaurantRatingJpaRepository.save(entity)).thenReturn(entity);

        restaurantRatingDbImpl.save(entity);

        verify(restaurantRatingJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingRatingForRestaurantAndUser_when_findByRestaurantIdAndUserId_then_returnsRating() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 45);
        when(restaurantRatingJpaRepository.findByRestaurantIdAndUserId(1L, 2L)).thenReturn(Optional.of(entity));

        Optional<RestaurantRatingEntity> result = restaurantRatingDbImpl.findByRestaurantIdAndUserId(1L, 2L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_noRatingForRestaurantAndUser_when_findByRestaurantIdAndUserId_then_returnsEmptyOptional() {
        when(restaurantRatingJpaRepository.findByRestaurantIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        Optional<RestaurantRatingEntity> result = restaurantRatingDbImpl.findByRestaurantIdAndUserId(1L, 99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullRestaurantId_when_findByRestaurantIdAndUserId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingDbImpl.findByRestaurantIdAndUserId(null, 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullUserId_when_findByRestaurantIdAndUserId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingDbImpl.findByRestaurantIdAndUserId(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_ratingsForRestaurant_when_findByRestaurantId_then_returnsRatings() {
        RestaurantRatingEntity entity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90, 60, 50, 45);
        when(restaurantRatingJpaRepository.findByRestaurantId(1L)).thenReturn(List.of(entity));

        List<RestaurantRatingEntity> result = restaurantRatingDbImpl.findByRestaurantId(1L);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_noRatingsForRestaurant_when_findByRestaurantId_then_returnsEmptyList() {
        when(restaurantRatingJpaRepository.findByRestaurantId(1L)).thenReturn(List.of());

        List<RestaurantRatingEntity> result = restaurantRatingDbImpl.findByRestaurantId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullRestaurantId_when_findByRestaurantId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingDbImpl.findByRestaurantId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
