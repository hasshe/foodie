package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemRatingMapper;
import com.hasshe.foodie.service.api.FoodItemRatingService;
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
class FoodItemRatingControllerTest {

    @Mock
    private FoodItemRatingService foodItemRatingService;

    @Mock
    private FoodItemRatingMapper foodItemRatingMapper;

    @InjectMocks
    private FoodItemRatingController foodItemRatingController;

    private final RateFoodItemDisplay rateRequest = new RateFoodItemDisplay(80, 70, 90, 60);

    @Test
    void given_validRequest_when_rateFoodItem_then_returnsMappedDisplay() {
        FoodItemRatingDomain domain = new FoodItemRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );
        FoodItemRatingDisplay display = new FoodItemRatingDisplay(1L, "Chef", 80, 70, 90, 60, 75.0);
        when(foodItemRatingService.rateFoodItem("chef123", 1L, rateRequest)).thenReturn(domain);
        when(foodItemRatingMapper.mapToDisplay(domain)).thenReturn(display);

        FoodItemRatingDisplay result = foodItemRatingController.rateFoodItem("chef123", 1L, rateRequest);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_rateFoodItem_then_delegatesToServiceExactlyOnce() {
        FoodItemRatingDomain domain = new FoodItemRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );
        when(foodItemRatingService.rateFoodItem("chef123", 1L, rateRequest)).thenReturn(domain);
        when(foodItemRatingMapper.mapToDisplay(domain)).thenReturn(
                new FoodItemRatingDisplay(1L, "Chef", 80, 70, 90, 60, 75.0));

        foodItemRatingController.rateFoodItem("chef123", 1L, rateRequest);

        verify(foodItemRatingService).rateFoodItem("chef123", 1L, rateRequest);
    }

    @Test
    void given_nullRateFoodItemDisplay_when_rateFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingController.rateFoodItem("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_rateFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingController.rateFoodItem("  ", 1L, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullFoodItemId_when_rateFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingController.rateFoodItem("chef123", null, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_rateFoodItem_then_propagatesException() {
        when(foodItemRatingService.rateFoodItem("chef123", 1L, rateRequest))
                .thenThrow(new ValidationException("You must be a member of the restaurant's group to rate this food item"));

        assertThatThrownBy(() -> foodItemRatingController.rateFoodItem("chef123", 1L, rateRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_foodItemWithRatings_when_getRatingSummary_then_returnsMappedSummaryDisplay() {
        FoodItemRatingSummaryDomain domain = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 80, 70, 90, 60, 75, 1, List.of()
        );
        FoodItemRatingSummaryDisplay display = new FoodItemRatingSummaryDisplay(
                1L, "Ribeye Steak", 80, 70, 90, 60, 75, 1, List.of(), null
        );
        when(foodItemRatingService.getRatingSummary("chef123", 1L)).thenReturn(domain);
        when(foodItemRatingMapper.mapToSummaryDisplay(domain, "chef123")).thenReturn(display);

        FoodItemRatingSummaryDisplay result = foodItemRatingController.getRatingSummary("chef123", 1L);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_foodItemWithNoRatings_when_getRatingSummary_then_returnsEmptySummaryDisplay() {
        FoodItemRatingSummaryDomain domain = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 0, 0, 0, 0, 0, 0, List.of()
        );
        FoodItemRatingSummaryDisplay display = new FoodItemRatingSummaryDisplay(
                1L, "Ribeye Steak", 0, 0, 0, 0, 0, 0, List.of(), null
        );
        when(foodItemRatingService.getRatingSummary("chef123", 1L)).thenReturn(domain);
        when(foodItemRatingMapper.mapToSummaryDisplay(domain, "chef123")).thenReturn(display);

        FoodItemRatingSummaryDisplay result = foodItemRatingController.getRatingSummary("chef123", 1L);

        assertThat(result.ratingCount()).isZero();
    }

    @Test
    void given_blankUsername_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingController.getRatingSummary("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullFoodItemId_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingController.getRatingSummary("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
