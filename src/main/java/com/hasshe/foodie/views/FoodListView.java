package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.controller.FoodItemController;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemWithRestaurantDisplay;
import com.hasshe.foodie.views.components.ListItemComponent;
import com.hasshe.foodie.views.components.RatingFormatter;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Route(value = RouteConstants.ROUTE_FOOD_LIST, layout = MainLayout.class)
@PageTitle("Foodlist | Foodie")
@PermitAll
public class FoodListView extends VerticalLayout implements BeforeEnterObserver {

    private static final String ALL_TYPES_LABEL = "All types";

    private final FoodItemController foodItemController;
    private final AuthenticationContext authenticationContext;

    private final RatingFormatter ratingFormatter = new RatingFormatter();

    private final Select<String> typeFilterSelect = new Select<>();
    private final VerticalLayout categoryGroupsLayout = new VerticalLayout();
    private final Span emptyStateMessage = new Span(
            "No food items to compare yet. Add the same dish at two or more restaurants to see it here.");

    private String currentUsername;
    private List<FoodItemCategoryGroupDisplay> allCategoryGroups = List.of();

    public FoodListView(FoodItemController foodItemController, AuthenticationContext authenticationContext) {
        this.foodItemController = foodItemController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        typeFilterSelect.setLabel("Filter by type");
        typeFilterSelect.setWidthFull();
        typeFilterSelect.addValueChangeListener(event -> renderCategoryGroups());

        categoryGroupsLayout.setPadding(false);
        categoryGroupsLayout.setWidthFull();
        categoryGroupsLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout card = new VerticalLayout(new H1("Foodlist"), typeFilterSelect, emptyStateMessage, categoryGroupsLayout);
        card.setAlignItems(Alignment.CENTER);
        card.setWidthFull();

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Foodlist view requires an authenticated user"));
        refreshFoodItems();
    }

    private void refreshFoodItems() {
        allCategoryGroups = foodItemController.listFoodItemsGroupedByCategory(currentUsername);
        refreshTypeFilterOptions();
        renderCategoryGroups();
    }

    private void refreshTypeFilterOptions() {
        List<String> items = new ArrayList<>();
        items.add(ALL_TYPES_LABEL);
        allCategoryGroups.stream().map(FoodItemCategoryGroupDisplay::dishCategory).forEach(items::add);

        String previousValue = typeFilterSelect.getValue();
        typeFilterSelect.setItems(items);
        typeFilterSelect.setValue(items.contains(previousValue) ? previousValue : ALL_TYPES_LABEL);
    }

    private void renderCategoryGroups() {
        String selectedType = typeFilterSelect.getValue();
        List<FoodItemCategoryGroupDisplay> visibleCategoryGroups = (selectedType == null || selectedType.equals(ALL_TYPES_LABEL))
                ? allCategoryGroups
                : allCategoryGroups.stream().filter(categoryGroup -> categoryGroup.dishCategory().equals(selectedType)).toList();

        categoryGroupsLayout.removeAll();
        emptyStateMessage.setVisible(visibleCategoryGroups.isEmpty());
        visibleCategoryGroups.forEach(categoryGroup -> categoryGroupsLayout.add(buildCategorySection(categoryGroup)));
    }

    private VerticalLayout buildCategorySection(FoodItemCategoryGroupDisplay categoryGroup) {
        VerticalLayout itemsLayout = new VerticalLayout();
        itemsLayout.setPadding(false);
        itemsLayout.setSpacing(false);
        itemsLayout.setWidthFull();
        itemsLayout.getStyle().set("max-width", "640px");

        categoryGroup.foodItems().forEach(foodItemWithRestaurantDisplay -> itemsLayout.add(new ListItemComponent(
                foodItemWithRestaurantDisplay.name(),
                foodItemWithRestaurantDisplay.restaurantName(),
                formatAverageRating(foodItemWithRestaurantDisplay),
                null
        ).asComponent()));

        VerticalLayout section = new VerticalLayout(new H2(categoryGroup.dishCategory()), itemsLayout);
        section.setPadding(false);
        section.setAlignItems(Alignment.CENTER);
        section.setWidthFull();
        return section;
    }

    private String formatAverageRating(FoodItemWithRestaurantDisplay foodItemWithRestaurantDisplay) {
        return ratingFormatter.format(foodItemWithRestaurantDisplay.averageRating(), foodItemWithRestaurantDisplay.ratingCount());
    }
}
