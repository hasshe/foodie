package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.controller.FoodItemController;
import com.hasshe.foodie.controller.FoodItemRatingController;
import com.hasshe.foodie.controller.GroupController;
import com.hasshe.foodie.controller.ProfileController;
import com.hasshe.foodie.controller.RestaurantController;
import com.hasshe.foodie.controller.RestaurantRatingController;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.views.components.AddRestaurantDialogComponent;
import com.hasshe.foodie.views.components.FoodItemListDialogComponent;
import com.hasshe.foodie.views.components.FoodItemRatingDialogComponent;
import com.hasshe.foodie.views.components.ListItemComponent;
import com.hasshe.foodie.views.components.NotificationComponent;
import com.hasshe.foodie.views.components.RatingFormatter;
import com.hasshe.foodie.views.components.RestaurantInfoDialogComponent;
import com.hasshe.foodie.views.components.RestaurantRatingDialogComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
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
import java.util.Comparator;
import java.util.List;

@Route(value = RouteConstants.ROUTE_RESTAURANTS, layout = MainLayout.class)
@PageTitle("Restaurants | Foodie")
@PermitAll
public class RestaurantsView extends VerticalLayout implements BeforeEnterObserver {

    private static final String ALL_GROUPS_LABEL = "All groups";

    private final RestaurantController restaurantController;
    private final GroupController groupController;
    private final ProfileController profileController;
    private final RestaurantRatingController restaurantRatingController;
    private final FoodItemController foodItemController;
    private final FoodItemRatingController foodItemRatingController;
    private final AuthenticationContext authenticationContext;

    private final RatingFormatter ratingFormatter = new RatingFormatter();
    private final NotificationComponent notificationComponent = new NotificationComponent();

    private final VerticalLayout restaurantListLayout = new VerticalLayout();
    private final Select<String> groupFilterSelect = new Select<>();

    private final AddRestaurantDialogComponent addRestaurantDialogComponent = new AddRestaurantDialogComponent("Add restaurant");
    private final RestaurantInfoDialogComponent restaurantInfoDialogComponent = new RestaurantInfoDialogComponent();
    private final RestaurantRatingDialogComponent restaurantRatingDialogComponent = new RestaurantRatingDialogComponent();
    private final FoodItemListDialogComponent foodItemListDialogComponent = new FoodItemListDialogComponent();
    private final FoodItemRatingDialogComponent foodItemRatingDialogComponent = new FoodItemRatingDialogComponent();

    private String currentUsername;
    private List<RestaurantDisplay> allRestaurants = List.of();

    public RestaurantsView(
            RestaurantController restaurantController,
            GroupController groupController,
            ProfileController profileController,
            RestaurantRatingController restaurantRatingController,
            FoodItemController foodItemController,
            FoodItemRatingController foodItemRatingController,
            AuthenticationContext authenticationContext
    ) {
        this.restaurantController = restaurantController;
        this.groupController = groupController;
        this.profileController = profileController;
        this.restaurantRatingController = restaurantRatingController;
        this.foodItemController = foodItemController;
        this.foodItemRatingController = foodItemRatingController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        restaurantListLayout.setPadding(false);
        restaurantListLayout.setSpacing(false);
        restaurantListLayout.setWidthFull();
        restaurantListLayout.getStyle().set("max-width", "640px");

        groupFilterSelect.setLabel("Filter by group");
        groupFilterSelect.setWidthFull();
        groupFilterSelect.addValueChangeListener(event -> renderRestaurantList());

        Button addRestaurantButton = new Button("Add restaurant", event -> openAddRestaurantDialog());
        addRestaurantButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addRestaurantButton.setWidthFull();

        VerticalLayout card = new VerticalLayout(new H1("Restaurants"), addRestaurantButton, groupFilterSelect, restaurantListLayout);
        card.setAlignItems(Alignment.CENTER);
        card.setWidthFull();

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Restaurants view requires an authenticated user"));
        refreshRestaurants();
        openRatingDialogFromQueryParameter(event);
    }

    private void refreshRestaurants() {
        allRestaurants = restaurantController.listRestaurantsForUser(currentUsername).stream()
                .sorted(Comparator.comparingDouble(RestaurantDisplay::averageRating).reversed())
                .toList();
        refreshGroupFilterOptions();
        renderRestaurantList();
    }

    private void refreshGroupFilterOptions() {
        List<String> groupNames = groupController.listGroupsForUser(currentUsername).stream()
                .map(GroupDisplay::name)
                .distinct()
                .sorted()
                .toList();
        List<String> items = new ArrayList<>();
        items.add(ALL_GROUPS_LABEL);
        items.addAll(groupNames);

        String previousValue = groupFilterSelect.getValue();
        groupFilterSelect.setItems(items);
        groupFilterSelect.setValue(items.contains(previousValue) ? previousValue : ALL_GROUPS_LABEL);
    }

    private void renderRestaurantList() {
        String selectedGroupName = groupFilterSelect.getValue();
        List<RestaurantDisplay> visibleRestaurants = (selectedGroupName == null || selectedGroupName.equals(ALL_GROUPS_LABEL))
                ? allRestaurants
                : allRestaurants.stream().filter(restaurantDisplay -> restaurantDisplay.groupName().equals(selectedGroupName)).toList();

        restaurantListLayout.removeAll();
        if (visibleRestaurants.isEmpty()) {
            restaurantListLayout.add(new Span("No restaurants yet."));
            return;
        }
        visibleRestaurants.forEach(restaurantDisplay -> restaurantListLayout.add(new ListItemComponent(
                restaurantDisplay.name(),
                restaurantDisplay.address() + " · " + restaurantDisplay.groupName(),
                formatAverageRating(restaurantDisplay),
                () -> openRestaurantInfoDialog(restaurantDisplay)
        ).asComponent()));
    }

    private void openRatingDialogFromQueryParameter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getParameters()
                .getOrDefault(RouteConstants.QUERY_PARAM_RATE_RESTAURANT_ID, List.of())
                .stream()
                .findFirst()
                .map(Long::valueOf)
                .flatMap(restaurantId -> allRestaurants.stream()
                        .filter(restaurantDisplay -> restaurantDisplay.id().equals(restaurantId))
                        .findFirst())
                .ifPresent(this::openRatingDialog);
    }

    private void openAddRestaurantDialog() {
        List<GroupDisplay> groups = groupController.listGroupsForUser(currentUsername);
        if (groups.isEmpty()) {
            notificationComponent.showError("You need to create a group before adding a restaurant.");
            getUI().ifPresent(ui -> ui.navigate(RouteConstants.ROUTE_GROUPS));
            return;
        }

        addRestaurantDialogComponent.open(groups, resolveDefaultGroup(groups), this::handleAddRestaurant);
    }

    private GroupDisplay resolveDefaultGroup(List<GroupDisplay> groups) {
        Long defaultGroupId = profileController.getProfile(currentUsername)
                .map(UserProfileDisplay::defaultGroup)
                .map(GroupDisplay::id)
                .orElse(null);
        return groups.stream()
                .filter(group -> group.id().equals(defaultGroupId))
                .findFirst()
                .orElse(groups.get(0));
    }

    private void handleAddRestaurant(AddRestaurantDisplay addRestaurantDisplay) {
        try {
            restaurantController.addRestaurant(currentUsername, addRestaurantDisplay);
            addRestaurantDialogComponent.close();
            notificationComponent.showSuccess("Restaurant added.");
            refreshRestaurants();
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private void openRestaurantInfoDialog(RestaurantDisplay restaurantDisplay) {
        restaurantInfoDialogComponent.open(
                restaurantDisplay,
                "Rate restaurant", () -> openRatingDialog(restaurantDisplay),
                "Food items", () -> openFoodItemsDialog(restaurantDisplay)
        );
    }

    private void openRatingDialog(RestaurantDisplay restaurantDisplay) {
        RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay =
                restaurantRatingController.getRatingSummary(currentUsername, restaurantDisplay.id());
        restaurantRatingDialogComponent.open(
                restaurantRatingSummaryDisplay,
                rateRestaurantDisplay -> handleRateSubmit(restaurantDisplay.id(), rateRestaurantDisplay)
        );
    }

    private void handleRateSubmit(Long restaurantId, RateRestaurantDisplay rateRestaurantDisplay) {
        try {
            restaurantRatingController.rateRestaurant(currentUsername, restaurantId, rateRestaurantDisplay);
            RestaurantRatingSummaryDisplay updatedSummary = restaurantRatingController.getRatingSummary(currentUsername, restaurantId);
            restaurantRatingDialogComponent.refresh(updatedSummary);
            refreshRestaurants();
            notificationComponent.showSuccess("Rating saved.");
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private void openFoodItemsDialog(RestaurantDisplay restaurantDisplay) {
        List<FoodItemDisplay> foodItems = foodItemController.listFoodItemsForRestaurant(currentUsername, restaurantDisplay.id());
        foodItemListDialogComponent.open(
                foodItems,
                (addFoodItemDisplay, rateFoodItemDisplay) -> handleAddFoodItem(restaurantDisplay.id(), addFoodItemDisplay, rateFoodItemDisplay),
                this::openFoodItemRatingDialog
        );
    }

    private void handleAddFoodItem(Long restaurantId, AddFoodItemDisplay addFoodItemDisplay, RateFoodItemDisplay rateFoodItemDisplay) {
        try {
            FoodItemDisplay addedFoodItem = foodItemController.addFoodItem(currentUsername, restaurantId, addFoodItemDisplay);
            foodItemRatingController.rateFoodItem(currentUsername, addedFoodItem.id(), rateFoodItemDisplay);
            List<FoodItemDisplay> updatedFoodItems = foodItemController.listFoodItemsForRestaurant(currentUsername, restaurantId);
            foodItemListDialogComponent.refresh(updatedFoodItems);
            notificationComponent.showSuccess("Food item added.");
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private void openFoodItemRatingDialog(FoodItemDisplay foodItemDisplay) {
        foodItemListDialogComponent.close();
        FoodItemRatingSummaryDisplay foodItemRatingSummaryDisplay =
                foodItemRatingController.getRatingSummary(currentUsername, foodItemDisplay.id());
        foodItemRatingDialogComponent.open(
                foodItemRatingSummaryDisplay,
                rateFoodItemDisplay -> handleRateFoodItemSubmit(foodItemDisplay.id(), rateFoodItemDisplay)
        );
    }

    private void handleRateFoodItemSubmit(Long foodItemId, RateFoodItemDisplay rateFoodItemDisplay) {
        try {
            foodItemRatingController.rateFoodItem(currentUsername, foodItemId, rateFoodItemDisplay);
            FoodItemRatingSummaryDisplay updatedSummary = foodItemRatingController.getRatingSummary(currentUsername, foodItemId);
            foodItemRatingDialogComponent.refresh(updatedSummary);
            notificationComponent.showSuccess("Rating saved.");
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private String formatAverageRating(RestaurantDisplay restaurantDisplay) {
        return ratingFormatter.format(restaurantDisplay.averageRating(), restaurantDisplay.ratingCount());
    }
}
