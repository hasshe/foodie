package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.RestaurantDb;
import com.hasshe.foodie.db.api.RestaurantRatingDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.db.entity.RestaurantRatingEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.RestaurantRatingDomain;
import com.hasshe.foodie.domain.RestaurantRatingSummaryDomain;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.RestaurantRatingMapper;
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
class RestaurantRatingServiceImplTest {

    @Mock
    private RestaurantRatingDb restaurantRatingDb;

    @Mock
    private RestaurantDb restaurantDb;

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private RestaurantRatingMapper restaurantRatingMapper;

    @InjectMocks
    private RestaurantRatingServiceImpl restaurantRatingServiceImpl;

    private final GroupEntity group = new GroupEntity("Foodies");
    private final RestaurantEntity restaurant = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);
    private final UserEntity rater = new UserEntity("chef123", "hashedPassword", "Chef");
    private final RateRestaurantDisplay rateRequest = new RateRestaurantDisplay(80, 70, 90);

    @Test
    void given_memberWithNoExistingRating_when_rateRestaurant_then_createsNewRatingAndReturnsDomain() {
        RestaurantRatingEntity savedEntity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90);
        RestaurantRatingDomain expected = new RestaurantRatingDomain(
                1L, 1L, "chef123", "Chef", 80, 70, 90, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(restaurantRatingDb.findByRestaurantIdAndUserId(1L, rater.getId())).thenReturn(Optional.empty());
        when(restaurantRatingDb.save(any(RestaurantRatingEntity.class))).thenReturn(savedEntity);
        when(restaurantRatingMapper.mapToDomain(savedEntity)).thenReturn(expected);

        RestaurantRatingDomain result = restaurantRatingServiceImpl.rateRestaurant("chef123", 1L, rateRequest);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_memberWithExistingRating_when_rateRestaurant_then_updatesExistingRatingInsteadOfCreatingNew() {
        RestaurantRatingEntity existingEntity = new RestaurantRatingEntity(restaurant, rater, 10, 10, 10);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(restaurantRatingDb.findByRestaurantIdAndUserId(1L, rater.getId())).thenReturn(Optional.of(existingEntity));
        when(restaurantRatingDb.save(existingEntity)).thenReturn(existingEntity);
        when(restaurantRatingMapper.mapToDomain(existingEntity)).thenReturn(
                new RestaurantRatingDomain(1L, 1L, "chef123", "Chef", 80, 70, 90, LocalDateTime.now(), LocalDateTime.now()));

        restaurantRatingServiceImpl.rateRestaurant("chef123", 1L, rateRequest);

        assertThat(existingEntity.getFood()).isEqualTo(80);
        verify(restaurantRatingDb, times(1)).save(existingEntity);
    }

    @Test
    void given_notMemberOfGroup_when_rateRestaurant_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(false);

        assertThatThrownBy(() -> restaurantRatingServiceImpl.rateRestaurant("chef123", 1L, rateRequest))
                .isInstanceOf(ValidationException.class);
        verify(restaurantRatingDb, never()).save(any());
    }

    @Test
    void given_unknownRestaurant_when_rateRestaurant_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantRatingServiceImpl.rateRestaurant("chef123", 99L, rateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_rateRestaurant_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantRatingServiceImpl.rateRestaurant("ghost", 1L, rateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullRateRestaurantDisplay_when_rateRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingServiceImpl.rateRestaurant("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_rateRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingServiceImpl.rateRestaurant("  ", 1L, rateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_memberOfGroup_when_getRatingSummary_then_returnsSummaryDomain() {
        RestaurantRatingEntity ratingEntity = new RestaurantRatingEntity(restaurant, rater, 80, 70, 90);
        RestaurantRatingSummaryDomain expected = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 80, 70, 90, 80, 1, List.of()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(restaurantRatingDb.findByRestaurantId(1L)).thenReturn(List.of(ratingEntity));
        when(restaurantRatingMapper.mapToSummaryDomain(restaurant, List.of(ratingEntity))).thenReturn(expected);

        RestaurantRatingSummaryDomain result = restaurantRatingServiceImpl.getRatingSummary("chef123", 1L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_restaurantWithNoRatings_when_getRatingSummary_then_returnsEmptySummary() {
        RestaurantRatingSummaryDomain expected = new RestaurantRatingSummaryDomain(
                1L, "The Diner", 0, 0, 0, 0, 0, List.of()
        );

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(true);
        when(restaurantRatingDb.findByRestaurantId(1L)).thenReturn(List.of());
        when(restaurantRatingMapper.mapToSummaryDomain(restaurant, List.of())).thenReturn(expected);

        RestaurantRatingSummaryDomain result = restaurantRatingServiceImpl.getRatingSummary("chef123", 1L);

        assertThat(result.ratingCount()).isZero();
    }

    @Test
    void given_notMemberOfGroup_when_getRatingSummary_then_throwsValidationException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(1L)).thenReturn(Optional.of(restaurant));
        when(groupDb.isMember(group.getId(), rater.getId())).thenReturn(false);

        assertThatThrownBy(() -> restaurantRatingServiceImpl.getRatingSummary("chef123", 1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_unknownRestaurant_when_getRatingSummary_then_throwsNotFoundException() {
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(rater));
        when(restaurantDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantRatingServiceImpl.getRatingSummary("chef123", 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownUsername_when_getRatingSummary_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantRatingServiceImpl.getRatingSummary("ghost", 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_getRatingSummary_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantRatingServiceImpl.getRatingSummary("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
