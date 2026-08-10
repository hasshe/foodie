package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.controller.FoodItemController;
import com.hasshe.foodie.dto.FoodItemCategoryGroupDisplay;
import com.hasshe.foodie.dto.FoodItemWithRestaurantDisplay;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Locale;

@Route(value = RouteConstants.ROUTE_FOOD_LIST, layout = MainLayout.class)
@PageTitle("FoodList | Foodie")
@PermitAll
public class FoodListView extends VerticalLayout implements BeforeEnterObserver {

    private final FoodItemController foodItemController;
    private final AuthenticationContext authenticationContext;

    private final VerticalLayout categoryGroupsLayout = new VerticalLayout();
    private final Span emptyStateMessage = new Span(
            "No food items to compare yet. Add the same dish at two or more restaurants to see it here.");

    private String currentUsername;

    public FoodListView(FoodItemController foodItemController, AuthenticationContext authenticationContext) {
        this.foodItemController = foodItemController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        categoryGroupsLayout.setPadding(false);
        categoryGroupsLayout.setWidthFull();
        categoryGroupsLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout card = new VerticalLayout(new H1("FoodList"), emptyStateMessage, categoryGroupsLayout);
        card.setAlignItems(Alignment.CENTER);
        card.setWidthFull();

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("FoodList view requires an authenticated user"));
        refreshFoodItems();
    }

    private void refreshFoodItems() {
        List<FoodItemCategoryGroupDisplay> categoryGroups = foodItemController.listFoodItemsGroupedByCategory(currentUsername);

        categoryGroupsLayout.removeAll();
        emptyStateMessage.setVisible(categoryGroups.isEmpty());
        categoryGroups.forEach(categoryGroup -> categoryGroupsLayout.add(buildCategorySection(categoryGroup)));
    }

    private VerticalLayout buildCategorySection(FoodItemCategoryGroupDisplay categoryGroup) {
        Grid<FoodItemWithRestaurantDisplay> grid = new Grid<>(FoodItemWithRestaurantDisplay.class, false);
        grid.addColumn(FoodItemWithRestaurantDisplay::name).setHeader("Name");
        grid.addColumn(FoodItemWithRestaurantDisplay::restaurantName).setHeader("Restaurant");
        grid.addColumn(this::formatAverageRating).setHeader("Rating");
        grid.setItems(categoryGroup.foodItems());
        grid.setWidthFull();
        grid.setAllRowsVisible(true);
        grid.getStyle().set("max-width", "640px");

        VerticalLayout section = new VerticalLayout(new H2(categoryGroup.dishCategory()), grid);
        section.setPadding(false);
        section.setAlignItems(Alignment.CENTER);
        section.setWidthFull();
        return section;
    }

    private String formatAverageRating(FoodItemWithRestaurantDisplay foodItemWithRestaurantDisplay) {
        if (foodItemWithRestaurantDisplay.ratingCount() == 0) {
            return "No ratings";
        }
        return String.format(Locale.US, "%.1f", foodItemWithRestaurantDisplay.averageRating());
    }
}
