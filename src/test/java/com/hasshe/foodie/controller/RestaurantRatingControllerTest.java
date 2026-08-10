package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantRatingMapper;
import com.hasshe.foodie.service.api.RestaurantRatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantRatingControllerTest {

    @Mock
    private RestaurantRatingService restaurantRatingService;

    @Mock
    private RestaurantRatingMapper restaurantRatingMapper;

    @InjectMocks
    private RestaurantRatingController restaurantRatingController;

    private final RateRestaurantDisplay rateRequest = new RateRestaurantDisplay(80, 70, 90);

    @Test
    void given_validRequest_when_rateRestaurant_then_returnsMappedDisplay() {
        RestaurantRatingDomain domain = new RestaurantRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, LocalDateTime.now(), LocalDateTime.now()
        );
        RestaurantRatingDisplay display = new RestaurantRatingDisplay(1L, "Chef", 80, 70, 90, 80.0);
        when(restaurantRatingService.rateRestaurant("chef123", 1L, rateRequest)).thenReturn(domain);
        when(restaurantRatingMapper.mapToDisplay(domain)).thenReturn(display);

        RestaurantRatingDisplay result = restaurantRatingController.rateRestaurant("chef123", 1L, rateRequest);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_rateRestaurant_then_delegatesToServiceExactlyOnce() {
        RestaurantRatingDomain domain = new RestaurantRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, LocalDateTime.now(), LocalDateTime.now()
        );
        when(restaurantRatingService.rateRestaurant("chef123", 1L, rateRequest)).thenReturn(domain);
        when(restaurantRatingMapper.mapToDisplay(domain)).thenReturn(
                new RestaurantRatingDisplay(1L, "Chef", 80, 70, 90, 80.0));

        restaurantRatingController.rateRestaurant("chef123", 1L, rateRequest);

        verify(restaurantRatingService).rateRestaurant("chef123", 1L, rateRequest);
    }

    @Test
    void given_nullRateRestaurantDisplay_when_rateRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingController.rateRestaurant("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_rateRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingController.rateRestaurant("  ", 1L, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRestaurantId_when_rateRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingController.rateRestaurant("chef123", null, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_rateRestaurant_then_propagatesException() {
        when(restaurantRatingService.rateRestaurant("chef123", 1L, rateRequest))
                .thenThrow(new ValidationException("You must be a member of the restaurant's group to rate it"));

        assertThatThrownBy(() -> restaurantRatingController.rateRestaurant("chef123", 1L, rateRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_restaurantWithRatings_when_getRatingSummary_then_returnsMappedSummaryDisplay() {
        RestaurantRatingSummaryDomain domain = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 80, 70, 90, 80, 1, List.of()
        );
        RestaurantRatingSummaryDisplay display = new RestaurantRatingSummaryDisplay(
                1L, "The Diner", 80, 70, 90, 80, 1, List.of(), null
        );
        when(restaurantRatingService.getRatingSummary("chef123", 1L)).thenReturn(domain);
        when(restaurantRatingMapper.mapToSummaryDisplay(domain, "chef123")).thenReturn(display);

        RestaurantRatingSummaryDisplay result = restaurantRatingController.getRatingSummary("chef123", 1L);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_restaurantWithNoRatings_when_getRatingSummary_then_returnsEmptySummaryDisplay() {
        RestaurantRatingSummaryDomain domain = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 0, 0, 0, 0, 0, List.of()
        );
        RestaurantRatingSummaryDisplay display = new RestaurantRatingSummaryDisplay(
                1L, "The Diner", 0, 0, 0, 0, 0, List.of(), null
        );
        when(restaurantRatingService.getRatingSummary("chef123", 1L)).thenReturn(domain);
        when(restaurantRatingMapper.mapToSummaryDisplay(domain, "chef123")).thenReturn(display);

        RestaurantRatingSummaryDisplay result = restaurantRatingController.getRatingSummary("chef123", 1L);

        assertThat(result.ratingCount()).isZero();
    }

    @Test
    void given_blankUsername_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingController.getRatingSummary("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRestaurantId_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingController.getRatingSummary("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
