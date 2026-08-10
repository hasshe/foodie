package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.FoodItemCategoryGroupDomain;
import com.hasshe.foodie.domain.FoodItemDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.FoodItemMapper;
import com.hasshe.foodie.service.api.FoodItemService;
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
class FoodItemControllerTest {

    @Mock
    private FoodItemService foodItemService;

    @Mock
    private FoodItemMapper foodItemMapper;

    @InjectMocks
    private FoodItemController foodItemController;

    @Test
    void given_validRequest_when_addFoodItem_then_returnsMappedDisplay() {
        AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null);
        FoodItemDisplay display = new FoodItemDisplay(1L, "Ribeye Steak", "Steak", 0.0, 0);
        when(foodItemService.addFoodItem("chef123", 1L, request)).thenReturn(domain);
        when(foodItemMapper.mapToDisplay(domain)).thenReturn(display);

        FoodItemDisplay result = foodItemController.addFoodItem("chef123", 1L, request);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_addFoodItem_then_delegatesToServiceExactlyOnce() {
        AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null);
        when(foodItemService.addFoodItem("chef123", 1L, request)).thenReturn(domain);
        when(foodItemMapper.mapToDisplay(domain)).thenReturn(new FoodItemDisplay(1L, "Ribeye Steak", "Steak", 0.0, 0));

        foodItemController.addFoodItem("chef123", 1L, request);

        verify(foodItemService).addFoodItem("chef123", 1L, request);
    }

    @Test
    void given_nullRequest_when_addFoodItem_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemController.addFoodItem("chef123", 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_addFoodItem_then_throwsIllegalArgumentException() {
        AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");

        assertThatThrownBy(() -> foodItemController.addFoodItem("  ", 1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRestaurantId_when_addFoodItem_then_throwsIllegalArgumentException() {
        AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");

        assertThatThrownBy(() -> foodItemController.addFoodItem("chef123", null, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_serviceThrowsValidationException_when_addFoodItem_then_propagatesException() {
        AddFoodItemDisplay request = new AddFoodItemDisplay("Ribeye Steak", "Steak");
        when(foodItemService.addFoodItem("chef123", 1L, request))
                .thenThrow(new ValidationException("You must be a member of the restaurant's group to add a food item"));

        assertThatThrownBy(() -> foodItemController.addFoodItem("chef123", 1L, request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_foodItems_when_listFoodItemsForRestaurant_then_returnsMappedDisplays() {
        FoodItemDomain domain = new FoodItemDomain(1L, 1L, "Ribeye Steak", "Steak", 0.0, 0, null, null);
        FoodItemDisplay display = new FoodItemDisplay(1L, "Ribeye Steak", "Steak", 0.0, 0);
        when(foodItemService.listFoodItemsForRestaurant("chef123", 1L)).thenReturn(List.of(domain));
        when(foodItemMapper.mapToDisplay(domain)).thenReturn(display);

        List<FoodItemDisplay> result = foodItemController.listFoodItemsForRestaurant("chef123", 1L);

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noFoodItems_when_listFoodItemsForRestaurant_then_returnsEmptyList() {
        when(foodItemService.listFoodItemsForRestaurant("chef123", 1L)).thenReturn(List.of());

        List<FoodItemDisplay> result = foodItemController.listFoodItemsForRestaurant("chef123", 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_listFoodItemsForRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemController.listFoodItemsForRestaurant("  ", 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullRestaurantId_when_listFoodItemsForRestaurant_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemController.listFoodItemsForRestaurant("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_categoryGroups_when_listFoodItemsGroupedByCategory_then_returnsMappedDisplays() {
        FoodItemCategoryGroupDomain domain = new FoodItemCategoryGroupDomain("Steak", List.of());
        FoodItemCategoryGroupDisplay display = new FoodItemCategoryGroupDisplay("Steak", List.of());
        when(foodItemService.listFoodItemsGroupedByCategory("chef123")).thenReturn(List.of(domain));
        when(foodItemMapper.mapToDisplay(domain)).thenReturn(display);

        List<FoodItemCategoryGroupDisplay> result = foodItemController.listFoodItemsGroupedByCategory("chef123");

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noCategoryGroups_when_listFoodItemsGroupedByCategory_then_returnsEmptyList() {
        when(foodItemService.listFoodItemsGroupedByCategory("chef123")).thenReturn(List.of());

        List<FoodItemCategoryGroupDisplay> result = foodItemController.listFoodItemsGroupedByCategory("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_listFoodItemsGroupedByCategory_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> foodItemController.listFoodItemsGroupedByCategory("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
