package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodItemServiceImplTest {

    @Mock
    private FoodItemDb foodItemDb;

    @Mock
    private FoodItemRatingDb foodItemRatingDb;

    @Mock
    private RestaurantDb restaurantDb;

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private FoodItemMapper foodItemMapper;

    @InjectMocks
    private FoodItemServiceImpl foodItemServiceImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
    private final AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");

    @Test
    void given_memberOfGroup_when_addFoodItem_then_returnsAddedDomain() {
        FoodItemEntity savedEntity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        FoodItemDomain expected = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(foodItemDb.save(any(FoodItemEntity.class))).thenReturn(savedEntity);
        when(foodItemMapper.mapToDomain(savedEntity, 0.0, 0)).thenReturn(expected);

        FoodItemDomain result = foodItemServiceImpl.addFoodItem("chef123", 1L, request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_memberOfGroup_when_addFoodItem_then_savesExactlyOnce() {
        FoodItemEntity savedEntity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(foodItemDb.save(any(FoodItemEntity.class))).thenReturn(savedEntity);
        when(foodItemMapper.mapToDomain(savedEntity, 0.0, 0)).thenReturn(
                new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null));

        foodItemServiceImpl.addFoodItem("chef123", 1L, request);

        verify(foodItemDb, times(1)).save(any(FoodItemEntity.class));
    }

    @Test
    void given_notMemberOfGroup_when_addFoodItem_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(false);

        assertThatThrownBy(() -> foodItemServiceImpl.addFoodItem("chef123", 1L, request))
                .isInstanceOf(ValidationException.class);
        verify(foodItemDb, never()).save(any());
    }

    @Test
    void given_unknownRestaurant_when_addFoodItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemServiceImpl.addFoodItem("chef123", 99L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_addFoodItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemServiceImpl.addFoodItem("ghost", 1L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullAddFoodItemDisplay_when_addFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemServiceImpl.addFoodItem("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_addFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemServiceImpl.addFoodItem("  ", 1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_restaurantWithRatedFoodItem_when_listFoodItemsForRestaurant_then_returnsFoodItemsWithAverageRating() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        UserEntity secondUser = new UserEntity("foodie99", "hashedPassword", "Foodie");
        FoodItemRatingEntity rating = new FoodItemRatingEntity(entity, secondUser, 100, 100, 100, 100);
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 100.0, 1, null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(foodItemDb.findByRestaurantId(1L)).thenReturn(List.of(entity));
        when(foodItemRatingDb.findByFoodItemId(entity.getId())).thenReturn(List.of(rating));
        when(foodItemMapper.mapToDomain(eq(entity), eq(100.0), eq(1))).thenReturn(domain);

        List<FoodItemDomain> result = foodItemServiceImpl.listFoodItemsForRestaurant("chef123", 1L);

        assertThat(result).containsExactly(domain);
    }

    @Test
    void given_restaurantWithUnratedFoodItem_when_listFoodItemsForRestaurant_then_returnsZeroAverageRating() {
        FoodItemEntity entity = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(foodItemDb.findByRestaurantId(1L)).thenReturn(List.of(entity));
        when(foodItemRatingDb.findByFoodItemId(entity.getId())).thenReturn(List.of());
        when(foodItemMapper.mapToDomain(eq(entity), eq(0.0), eq(0))).thenReturn(domain);

        List<FoodItemDomain> result = foodItemServiceImpl.listFoodItemsForRestaurant("chef123", 1L);

        assertThat(result).containsExactly(domain);
    }

    @Test
    void given_restaurantWithNoFoodItems_when_listFoodItemsForRestaurant_then_returnsEmptyList() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(foodItemDb.findByRestaurantId(1L)).thenReturn(List.of());

        List<FoodItemDomain> result = foodItemServiceImpl.listFoodItemsForRestaurant("chef123", 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_notMemberOfGroup_when_listFoodItemsForRestaurant_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(false);

        assertThatThrownBy(() -> foodItemServiceImpl.listFoodItemsForRestaurant("chef123", 1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_unknownRestaurant_when_listFoodItemsForRestaurant_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemServiceImpl.listFoodItemsForRestaurant("chef123", 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_listFoodItemsForRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemServiceImpl.listFoodItemsForRestaurant("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
