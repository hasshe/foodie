package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantMapper;
import com.hasshe.foodie.service.api.RestaurantService;
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
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantController restaurantController;

    @Test
    void given_validRequest_when_addRestaurant_then_returnsMappedDisplay() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        RestaurantDisplay display = new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0);
        when(restaurantService.addRestaurant("chef123", request)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(display);

        RestaurantDisplay result = restaurantController.addRestaurant("chef123", request);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_addRestaurant_then_delegatesToServiceExactlyOnce() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        when(restaurantService.addRestaurant("chef123", request)).thenReturn(domain);
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(
                new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0));

        restaurantController.addRestaurant("chef123", request);

        verify(restaurantService).addRestaurant("chef123", request);
    }

    @Test
    void given_nullRequest_when_addRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantController.addRestaurant("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_addRestaurant_then_throwsIllegalArgumentException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);

        assertThatThrownBy(() -> restaurantController.addRestaurant("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_addRestaurant_then_propagatesException() {
        AddRestaurantDisplay request = new AddRestaurantDisplay("The Diner", "123 Main St", null, null, null, 1L);
        when(restaurantService.addRestaurant("chef123", request))
                .thenThrow(new ValidationException("You must be a member of the group to add a restaurant to it"));

        assertThatThrownBy(() -> restaurantController.addRestaurant("chef123", request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_restaurants_when_listRestaurantsForUser_then_returnsMappedDisplays() {
        RestaurantDomain domain = new RestaurantDomain(1L, "The Diner", "123 Main St", null, null, null, null, 0.0, 0, null, null);
        RestaurantDisplay display = new RestaurantDisplay(1L, "The Diner", "123 Main St", null, null, null, "Foodies", 0.0, 0);
        when(restaurantService.listRestaurantsForUser("chef123")).thenReturn(List.of(domain));
        when(restaurantMapper.mapToDisplay(domain)).thenReturn(display);

        List<RestaurantDisplay> result = restaurantController.listRestaurantsForUser("chef123");

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noRestaurants_when_listRestaurantsForUser_then_returnsEmptyList() {
        when(restaurantService.listRestaurantsForUser("chef123")).thenReturn(List.of());

        List<RestaurantDisplay> result = restaurantController.listRestaurantsForUser("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_listRestaurantsForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantController.listRestaurantsForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
