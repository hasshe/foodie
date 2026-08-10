package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.WishlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    void given_validRequest_when_addToWishlist_then_returnsMappedDisplay() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        RestaurantDisplay display = new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0);
        when(wishlistService.addToWishlist("chef123", request)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(display);

        RestaurantDisplay result = wishlistController.addToWishlist("chef123", request);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_addToWishlist_then_delegatesToServiceExactlyOnce() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        when(wishlistService.addToWishlist("chef123", request)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(
                new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0));

        wishlistController.addToWishlist("chef123", request);

        verify(wishlistService).addToWishlist("chef123", request);
    }

    @Test
    void given_nullRequest_when_addToWishlist_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistController.addToWishlist("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_addToWishlist_then_throwsIllegalArgumentException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        assertThatThrownBy(() -> wishlistController.addToWishlist("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_addToWishlist_then_propagatesException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        when(wishlistService.addToWishlist("chef123", request))
                .thenThrow(new ValidationException("You must be a member of the group to add a restaurant to its wishlist"));

        assertThatThrownBy(() -> wishlistController.addToWishlist("chef123", request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_wishlistItems_when_listWishlistForUser_then_returnsMappedDisplays() {
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        RestaurantDisplay display = new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0);
        when(wishlistService.listWishlistForUser("chef123")).thenReturn(List.of(domain));
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(display);

        List<RestaurantDisplay> result = wishlistController.listWishlistForUser("chef123");

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noWishlistItems_when_listWishlistForUser_then_returnsEmptyList() {
        when(wishlistService.listWishlistForUser("chef123")).thenReturn(List.of());

        List<RestaurantDisplay> result = wishlistController.listWishlistForUser("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_listWishlistForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistController.listWishlistForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validRequest_when_checkOffWishlistItem_then_returnsMappedDisplay() {
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        RestaurantDisplay display = new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0);
        when(wishlistService.checkOffWishlistItem("chef123", 1L)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(display);

        RestaurantDisplay result = wishlistController.checkOffWishlistItem("chef123", 1L);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_checkOffWishlistItem_then_delegatesToServiceExactlyOnce() {
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        when(wishlistService.checkOffWishlistItem("chef123", 1L)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(
                new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0));

        wishlistController.checkOffWishlistItem("chef123", 1L);

        verify(wishlistService).checkOffWishlistItem("chef123", 1L);
    }

    @Test
    void given_blankUsername_when_checkOffWishlistItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistController.checkOffWishlistItem("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRestaurantId_when_checkOffWishlistItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> wishlistController.checkOffWishlistItem("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_checkOffWishlistItem_then_propagatesException() {
        when(wishlistService.checkOffWishlistItem("chef123", 1L))
                .thenThrow(new ValidationException("You must be a member of the restaurant's group to check it off the wishlist"));

        assertThatThrownBy(() -> wishlistController.checkOffWishlistItem("chef123", 1L))
                .isInstanceOf(ValidationException.class);
    }
}
