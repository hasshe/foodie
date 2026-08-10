package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RestaurantConstants;
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
import com.hasshe.foodie.views.components.FoodItemListDialogComponent;
import com.hasshe.foodie.views.components.FoodItemRatingDialogComponent;
import com.hasshe.foodie.views.components.RestaurantRatingDialogComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Route(value = RouteConstants.ROUTE_RESTAURANTS, layout = MainLayout.class)
@PageTitle("Restaurants | Foodie")
@PermitAll
public class RestaurantsView extends VerticalLayout implements BeforeEnterObserver {

    private final RestaurantController restaurantController;
    private final GroupController groupController;
    private final ProfileController profileController;
    private final RestaurantRatingController restaurantRatingController;
    private final FoodItemController foodItemController;
    private final FoodItemRatingController foodItemRatingController;
    private final AuthenticationContext authenticationContext;

    private final Grid<RestaurantDisplay> restaurantGrid = new Grid<>(RestaurantDisplay.class, false);

    private final Dialog addRestaurantDialog = new Dialog();
    private final TextField nameField = new TextField("Name");
    private final TextField addressField = new TextField("Address");
    private final TextField cuisineTypeField = new TextField("Cuisine type");
    private final TextField websiteField = new TextField("Website");
    private final Select<GroupDisplay> groupSelect = new Select<>();

    private final RestaurantRatingDialogComponent restaurantRatingDialogComponent = new RestaurantRatingDialogComponent();
    private final FoodItemListDialogComponent foodItemListDialogComponent = new FoodItemListDialogComponent();
    private final FoodItemRatingDialogComponent foodItemRatingDialogComponent = new FoodItemRatingDialogComponent();

    private String currentUsername;

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

        restaurantGrid.addColumn(RestaurantDisplay::name).setHeader("Name");
        restaurantGrid.addColumn(RestaurantDisplay::address).setHeader("Address");
        restaurantGrid.addColumn(RestaurantDisplay::groupName).setHeader("Group");
        restaurantGrid.setWidth("640px");
        restaurantGrid.getStyle().set("align-self", "center");
        restaurantGrid.addItemClickListener(event -> openRatingDialog(event.getItem()));

        Button addRestaurantButton = new Button("Add restaurant", event -> openAddRestaurantDialog());
        addRestaurantButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buildAddRestaurantDialog();

        VerticalLayout card = new VerticalLayout(new H1("Restaurants"), addRestaurantButton, restaurantGrid);
        card.setAlignItems(Alignment.CENTER);

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Restaurants view requires an authenticated user"));
        refreshRestaurants();
    }

    private void refreshRestaurants() {
        restaurantGrid.setItems(restaurantController.listRestaurantsForUser(currentUsername));
    }

    private void buildAddRestaurantDialog() {
        addRestaurantDialog.setHeaderTitle("Add restaurant");

        nameField.setRequiredIndicatorVisible(true);
        nameField.setMaxLength(RestaurantConstants.NAME_MAX_LENGTH);
        nameField.setWidthFull();

        addressField.setRequiredIndicatorVisible(true);
        addressField.setMaxLength(RestaurantConstants.ADDRESS_MAX_LENGTH);
        addressField.setWidthFull();

        cuisineTypeField.setMaxLength(RestaurantConstants.CUISINE_TYPE_MAX_LENGTH);
        cuisineTypeField.setWidthFull();

        websiteField.setMaxLength(RestaurantConstants.WEBSITE_MAX_LENGTH);
        websiteField.setWidthFull();

        groupSelect.setLabel("Group");
        groupSelect.setRequiredIndicatorVisible(true);
        groupSelect.setWidthFull();
        groupSelect.setItemLabelGenerator(this::generateGroupLabel);

        VerticalLayout formLayout = new VerticalLayout(
                nameField, addressField, cuisineTypeField, websiteField, groupSelect
        );
        formLayout.setPadding(false);
        formLayout.setWidth("320px");

        Button saveButton = new Button("Add", event -> addRestaurant());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", event -> addRestaurantDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttons);
        dialogLayout.setPadding(false);
        addRestaurantDialog.add(dialogLayout);
    }

    private void openAddRestaurantDialog() {
        List<GroupDisplay> groups = groupController.listGroupsForUser(currentUsername);
        if (groups.isEmpty()) {
            Notification errorNotification = Notification.show("You need to create a group before adding a restaurant.");
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(RouteConstants.ROUTE_GROUPS));
            return;
        }

        nameField.clear();
        addressField.clear();
        cuisineTypeField.clear();
        websiteField.clear();

        groupSelect.setItems(groups);
        Long defaultGroupId = profileController.getProfile(currentUsername)
                .map(UserProfileDisplay::defaultGroup)
                .map(GroupDisplay::id)
                .orElse(null);
        GroupDisplay defaultGroup = groups.stream()
                .filter(group -> group.id().equals(defaultGroupId))
                .findFirst()
                .orElse(groups.get(0));
        groupSelect.setValue(defaultGroup);

        addRestaurantDialog.open();
    }

    private void addRestaurant() {
        if (nameField.isEmpty() || addressField.isEmpty() || groupSelect.isEmpty()) {
            Notification.show("Please fill in the required fields.");
            return;
        }

        try {
            restaurantController.addRestaurant(currentUsername, new AddRestaurantDisplay(
                    nameField.getValue(),
                    addressField.getValue(),
                    blankToNull(cuisineTypeField.getValue()),
                    blankToNull(websiteField.getValue()),
                    null,
                    groupSelect.getValue().id()
            ));
            addRestaurantDialog.close();
            Notification success = Notification.show("Restaurant added.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshRestaurants();
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openRatingDialog(RestaurantDisplay restaurantDisplay) {
        RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay =
                restaurantRatingController.getRatingSummary(currentUsername, restaurantDisplay.id());
        restaurantRatingDialogComponent.open(
                restaurantRatingSummaryDisplay,
                rateRestaurantDisplay -> handleRateSubmit(restaurantDisplay.id(), rateRestaurantDisplay),
                () -> openFoodItemsDialog(restaurantDisplay)
        );
    }

    private void handleRateSubmit(Long restaurantId, RateRestaurantDisplay rateRestaurantDisplay) {
        try {
            restaurantRatingController.rateRestaurant(currentUsername, restaurantId, rateRestaurantDisplay);
            RestaurantRatingSummaryDisplay updatedSummary = restaurantRatingController.getRatingSummary(currentUsername, restaurantId);
            restaurantRatingDialogComponent.refresh(updatedSummary);
            Notification success = Notification.show("Rating saved.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openFoodItemsDialog(RestaurantDisplay restaurantDisplay) {
        restaurantRatingDialogComponent.close();
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
            Notification success = Notification.show("Food item added.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
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
            Notification success = Notification.show("Rating saved.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private String generateGroupLabel(GroupDisplay groupDisplay) {
        return groupDisplay == null ? "" : groupDisplay.name();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
