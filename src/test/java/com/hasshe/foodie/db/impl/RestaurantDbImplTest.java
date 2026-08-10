package com.hasshe.foodie.db.impl;

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
class RestaurantDbImplTest {

    @Mock
    private RestaurantJpaRepository restaurantJpaRepository;

    @InjectMocks
    private RestaurantDbImpl restaurantDbImpl;

    private final GroupEntity group = new GroupEntity("Foodies");

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        when(restaurantJpaRepository.save(entity)).thenReturn(entity);

        RestaurantEntity result = restaurantDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        RestaurantEntity entity = new RestaurantEntity("Pizza Place", "456 Oak Ave", group, null, null, null);
        when(restaurantJpaRepository.save(entity)).thenReturn(entity);

        restaurantDbImpl.save(entity);

        verify(restaurantJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingId_when_findById_then_returnsEntity() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        when(restaurantJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<RestaurantEntity> result = restaurantDbImpl.findById(1L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownId_when_findById_then_returnsEmptyOptional() {
        when(restaurantJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<RestaurantEntity> result = restaurantDbImpl.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantDbImpl.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_matchingGroupIds_when_findByGroupIdInAndWishlist_then_returnsVisitedRestaurants() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        when(restaurantJpaRepository.findByGroupIdInAndWishlist(List.of(1L), false)).thenReturn(List.of(entity));

        List<RestaurantEntity> result = restaurantDbImpl.findByGroupIdInAndWishlist(List.of(1L), false);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_matchingGroupIds_when_findByGroupIdInAndWishlistTrue_then_returnsWishlistRestaurants() {
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);
        when(restaurantJpaRepository.findByGroupIdInAndWishlist(List.of(1L), true)).thenReturn(List.of(entity));

        List<RestaurantEntity> result = restaurantDbImpl.findByGroupIdInAndWishlist(List.of(1L), true);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_emptyGroupIdList_when_findByGroupIdInAndWishlist_then_returnsEmptyList() {
        when(restaurantJpaRepository.findByGroupIdInAndWishlist(List.of(), false)).thenReturn(List.of());

        List<RestaurantEntity> result = restaurantDbImpl.findByGroupIdInAndWishlist(List.of(), false);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullGroupIds_when_findByGroupIdInAndWishlist_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantDbImpl.findByGroupIdInAndWishlist(null, false))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
