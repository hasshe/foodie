package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.FoodItemDb;
import com.hasshe.foodie.db.api.FoodItemRatingDb;
import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.FoodItemEntity;
import com.hasshe.foodie.db.entity.FoodItemRatingEntity;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.FoodItemRatingDomain;
import com.hasshe.foodie.domain.FoodItemRatingSummaryDomain;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemRatingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
class FoodItemRatingServiceImplTest {

    @Mock
    private FoodItemRatingDb foodItemRatingDb;

    @Mock
    private FoodItemDb foodItemDb;

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private FoodItemRatingMapper foodItemRatingMapper;

    @InjectMocks
    private FoodItemRatingServiceImpl foodItemRatingServiceImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final FoodItemEntity foodItem = new FoodItemEntity(restaurant, "Ribeye Steak", "Steak");
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");
    private final RateFoodItemDisplay rateRequest = new RateFoodItemDisplay(80, 70, 90, 60);

    @Test
    void given_memberWithNoExistingRating_when_rateFoodItem_then_createsNewRatingAndReturnsDomain() {
        FoodItemRatingEntity savedEntity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);
        FoodItemRatingDomain expected = new FoodItemRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(foodItemRatingDb.findByFoodItemIdAndUserId(1L, rater.getId())).thenReturn(Optional.empty());
        when(foodItemRatingDb.save(any(FoodItemRatingEntity.class))).thenReturn(savedEntity);
        when(foodItemRatingMapper.mapToDomain(savedEntity)).thenReturn(expected);

        FoodItemRatingDomain result = foodItemRatingServiceImpl.rateFoodItem("chef123", 1L, rateRequest);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_memberWithExistingRating_when_rateFoodItem_then_updatesExistingRatingInsteadOfCreatingNew() {
        FoodItemRatingEntity existingEntity = new FoodItemRatingEntity(foodItem, rater, 10, 10, 10, 10);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(foodItemRatingDb.findByFoodItemIdAndUserId(1L, rater.getId())).thenReturn(Optional.of(existingEntity));
        when(foodItemRatingDb.save(existingEntity)).thenReturn(existingEntity);
        when(foodItemRatingMapper.mapToDomain(existingEntity)).thenReturn(
                new FoodItemRatingDomain(1L, 1L, "chef123", "Chef", 80, 70, 90, 60, LocalDateTime.now(), LocalDateTime.now()));

        foodItemRatingServiceImpl.rateFoodItem("chef123", 1L, rateRequest);

        assertThat(existingEntity.getTaste()).isEqualTo(80);
        verify(foodItemRatingDb, times(1)).save(existingEntity);
    }

    @Test
    void given_notMemberOfGroup_when_rateFoodItem_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(false);

        assertThatThrownBy(() -> foodItemRatingServiceImpl.rateFoodItem("chef123", 1L, rateRequest))
                .isInstanceOf(ValidationException.class);
        verify(foodItemRatingDb, never()).save(any());
    }

    @Test
    void given_unknownFoodItem_when_rateFoodItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemRatingServiceImpl.rateFoodItem("chef123", 99L, rateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_rateFoodItem_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemRatingServiceImpl.rateFoodItem("ghost", 1L, rateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullRateFoodItemDisplay_when_rateFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingServiceImpl.rateFoodItem("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_rateFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingServiceImpl.rateFoodItem("  ", 1L, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_memberOfGroup_when_getRatingSummary_then_returnsSummaryDomain() {
        FoodItemRatingEntity ratingEntity = new FoodItemRatingEntity(foodItem, rater, 80, 70, 90, 60);
        FoodItemRatingSummaryDomain expected = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 80, 70, 90, 60, 75, 1, List.of()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(foodItemRatingDb.findByFoodItemId(1L)).thenReturn(List.of(ratingEntity));
        when(foodItemRatingMapper.mapToSummaryDomain(foodItem, List.of(ratingEntity))).thenReturn(expected);

        FoodItemRatingSummaryDomain result = foodItemRatingServiceImpl.getRatingSummary("chef123", 1L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_foodItemWithNoRatings_when_getRatingSummary_then_returnsEmptySummary() {
        FoodItemRatingSummaryDomain expected = new FoodItemRatingSummaryDomain(
                1L, "Ribeye Steak", 0, 0, 0, 0, 0, 0, List.of()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(foodItemRatingDb.findByFoodItemId(1L)).thenReturn(List.of());
        when(foodItemRatingMapper.mapToSummaryDomain(foodItem, List.of())).thenReturn(expected);

        FoodItemRatingSummaryDomain result = foodItemRatingServiceImpl.getRatingSummary("chef123", 1L);

        assertThat(result.ratingCount()).isZero();
    }

    @Test
    void given_notMemberOfGroup_when_getRatingSummary_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(1L)).thenReturn(Optional.of(foodItem));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(false);

        assertThatThrownBy(() -> foodItemRatingServiceImpl.getRatingSummary("chef123", 1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_unknownFoodItem_when_getRatingSummary_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(foodItemDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemRatingServiceImpl.getRatingSummary("chef123", 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_getRatingSummary_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemRatingServiceImpl.getRatingSummary("ghost", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemRatingServiceImpl.getRatingSummary("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
