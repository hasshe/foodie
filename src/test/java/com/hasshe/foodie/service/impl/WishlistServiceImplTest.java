package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
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
class WishlistServiceImplTest {

    @Mock
    private RestaurantDb restaurantDb;

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private WishlistServiceImpl wishlistServiceImpl;

    @Test
    void given_memberOfGroup_when_addToWishlist_then_returnsAddedDomain() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity savedEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);
        RestaurantDomain expected = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(true);
        when(restaurantDb.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
        when(restaurantMapper.mapToDomain(savedEntity, 0.0, 0)).thenReturn(expected);

        RestaurantDomain result = wishlistServiceImpl.addToWishlist("chef123", request);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_memberOfGroup_when_addToWishlist_then_savesRestaurantWithWishlistTrue() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity savedEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(true);
        when(restaurantDb.save(any(RestaurantEntity.class))).thenReturn(savedEntity);
        when(restaurantMapper.mapToDomain(savedEntity, 0.0, 0)).thenReturn(
                new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null));

        wishlistServiceImpl.addToWishlist("chef123", request);

        verify(restaurantDb, times(1)).save(any(RestaurantEntity.class));
    }

    @Test
    void given_notMemberOfGroup_when_addToWishlist_then_throwsValidationException() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(1L)).thenReturn(Optional.of(group));
        when(groupDb.isMember(1L, user.getId())).thenReturn(false);

        assertThatThrownBy(() -> wishlistServiceImpl.addToWishlist("chef123", request))
                .isInstanceOf(ValidationException.class);
        verify(restaurantDb, never()).save(any());
    }

    @Test
    void given_unknownGroup_when_addToWishlist_then_throwsNotFoundException() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 99L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistServiceImpl.addToWishlist("chef123", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_addToWishlist_then_throwsNotFoundException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistServiceImpl.addToWishlist("ghost", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullAddRestaurantDisplay_when_addToWishlist_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistServiceImpl.addToWishlist("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_addToWishlist_then_throwsIllegalArgumentException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        assertThatThrownBy(() -> wishlistServiceImpl.addToWishlist("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_userWithWishlistItems_when_listWishlistForUser_then_returnsMappedWishlistItems() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurantEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);
        RestaurantDomain restaurantDomain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of(group));
        when(restaurantDb.findByGroupIdInAndWishlist(Collections.singletonList(group.getId()), true)).thenReturn(List.of(restaurantEntity));
        when(restaurantMapper.mapToDomain(restaurantEntity, 0.0, 0)).thenReturn(restaurantDomain);

        List<RestaurantDomain> result = wishlistServiceImpl.listWishlistForUser("chef123");

        assertThat(result).containsExactly(restaurantDomain);
    }

    @Test
    void given_userWithNoGroups_when_listWishlistForUser_then_returnsEmptyList() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of());

        List<RestaurantDomain> result = wishlistServiceImpl.listWishlistForUser("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_unknownUsername_when_listWishlistForUser_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistServiceImpl.listWishlistForUser("ghost"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_listWishlistForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistServiceImpl.listWishlistForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_memberOfGroup_when_checkOffWishlistItem_then_marksVisitedAndReturnsDomain() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurantEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);
        RestaurantDomain expected = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurantEntity));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(restaurantDb.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(restaurantMapper.mapToDomain(restaurantEntity, 0.0, 0)).thenReturn(expected);

        RestaurantDomain result = wishlistServiceImpl.checkOffWishlistItem("chef123", 1L);

        assertThat(result).isEqualTo(expected);
        assertThat(restaurantEntity.isWishlist()).isFalse();
    }

    @Test
    void given_memberOfGroup_when_checkOffWishlistItem_then_savesExactlyOnce() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurantEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurantEntity));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(true);
        when(restaurantDb.save(restaurantEntity)).thenReturn(restaurantEntity);
        when(restaurantMapper.mapToDomain(restaurantEntity, 0.0, 0)).thenReturn(
                new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null));

        wishlistServiceImpl.checkOffWishlistItem("chef123", 1L);

        verify(restaurantDb, times(1)).save(restaurantEntity);
    }

    @Test
    void given_notMemberOfGroup_when_checkOffWishlistItem_then_throwsValidationException() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity restaurantEntity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null, true);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurantEntity));
        when(groupDb.isMember(group.getId(), user.getId())).thenReturn(false);

        assertThatThrownBy(() -> wishlistServiceImpl.checkOffWishlistItem("chef123", 1L))
                .isInstanceOf(ValidationException.class);
        verify(restaurantDb, never()).save(any());
    }

    @Test
    void given_unknownRestaurant_when_checkOffWishlistItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(new UserEntity("chef123", "hashedPassword", "Chef")));
        when(restaurantDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistServiceImpl.checkOffWishlistItem("chef123", 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_checkOffWishlistItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistServiceImpl.checkOffWishlistItem("ghost", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullRestaurantId_when_checkOffWishlistItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistServiceImpl.checkOffWishlistItem("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_checkOffWishlistItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistServiceImpl.checkOffWishlistItem("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
