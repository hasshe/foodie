package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantDb restaurantDb;

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantServiceImpl restaurantServiceImpl;

    @Test
    void given_memberOfGroup_when_addRestaurant_then_returnsAddedDomain() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity savedEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        RestaurantDomain expected = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, null, null);
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(true);
        when(restaurantDb.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
        when(restaurantMapper.mapToDomain(savedEntity)).thenReturn(expected);

        RestaurantDomain result = restaurantServiceImpl.addRestaurant("chef123", request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_memberOfGroup_when_addRestaurant_then_savesExactlyOnce() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity savedEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(true);
        when(restaurantDb.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
        when(restaurantMapper.mapToDomain(savedEntity)).thenReturn(
                new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, null, null));

        restaurantServiceImpl.addRestaurant("chef123", request);

        verify(restaurantDb, times(1)).save(any(RestaurantEntity.class));
    }

    @Test
    void given_notMemberOfGroup_when_addRestaurant_then_throwsValidationException() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(false);

        assertThatThrownBy(() -> restaurantServiceImpl.addRestaurant("chef123", request))
                .isInstanceOf(ValidationException.class);
        verify(restaurantDb, never()).save(any());
    }

    @Test
    void given_unknownGroup_when_addRestaurant_then_throwsNotFoundException() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 99L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantServiceImpl.addRestaurant("chef123", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_addRestaurant_then_throwsNotFoundException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantServiceImpl.addRestaurant("ghost", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullAddRestaurantDisplay_when_addRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantServiceImpl.addRestaurant("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_allOptionalFieldsProvided_when_addRestaurant_then_returnsDomainWithOptionalFields() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity savedEntity = new RestaurantEntity(
                "The Diner", "123 Main St", group, "American", "https://diner.example", "555-1234"
        );
        RestaurantDomain expected = new RestaurantDomain(
                1L, "The Diner", "123 Main St", "American", "https://diner.example", "555-1234", null, null, null
        );
        AddRestaurantDisplay request = new AddRestaurantDisplay(
                "The Diner", "123 Main St", "American", "https://diner.example", "555-1234", 1L
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(true);
        when(restaurantDb.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
        when(restaurantMapper.mapToDomain(savedEntity)).thenReturn(expected);

        RestaurantDomain result = restaurantServiceImpl.addRestaurant("chef123", request);

        assertThat(result.cuisineType()).isEqualTo("American");
    }

    @Test
    void given_userWithGroupsAndRestaurants_when_listRestaurantsForUser_then_returnsMappedRestaurants() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurantEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
        RestaurantDomain restaurantDomain = new RestaurantDomain(
                1L, "The Diner", "123 Main St", null, null, null, null, null, null
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of(group));
        when(restaurantDb.findByGroupIdIn(Collections.singletonList(group.getId()))).thenReturn(List.of(restaurantEntity));
        when(restaurantMapper.mapToDomain(restaurantEntity)).thenReturn(restaurantDomain);

        List<RestaurantDomain> result = restaurantServiceImpl.listRestaurantsForUser("chef123");

        assertThat(result).containsExactly(restaurantDomain);
    }

    @Test
    void given_userWithNoGroups_when_listRestaurantsForUser_then_returnsEmptyListWithoutQueryingRestaurants() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of());

        List<RestaurantDomain> result = restaurantServiceImpl.listRestaurantsForUser("chef123");

        assertThat(result).isEmpty();
        verify(restaurantDb, never()).findByGroupIdIn(any());
    }

    @Test
    void given_unknownUsername_when_listRestaurantsForUser_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantServiceImpl.listRestaurantsForUser("ghost"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_listRestaurantsForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantServiceImpl.listRestaurantsForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
