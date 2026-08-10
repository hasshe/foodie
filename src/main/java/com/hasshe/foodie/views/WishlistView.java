package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.controller.GroupController;
import com.hasshe.foodie.controller.ProfileController;
import com.hasshe.foodie.controller.WishlistController;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.dto.RestaurantDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.views.components.AddRestaurantDialogComponent;
import com.hasshe.foodie.views.components.CheckOffPromptDialogComponent;
import com.hasshe.foodie.views.components.RestaurantInfoDialogComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@Route(value = RouteConstants.ROUTE_WISHLIST, layout = MainLayout.class)
@PageTitle("Wishlist | Foodie")
@PermitAll
public class WishlistView extends VerticalLayout implements BeforeEnterObserver {

    private final WishlistController wishlistController;
    private final GroupController groupController;
    private final ProfileController profileController;
    private final AuthenticationContext authenticationContext;

    private final Grid<RestaurantDisplay> wishlistGrid = new Grid<>(RestaurantDisplay.class, false);

    private final AddRestaurantDialogComponent addToWishlistDialogComponent = new AddRestaurantDialogComponent("Add to wishlist");
    private final RestaurantInfoDialogComponent restaurantInfoDialogComponent = new RestaurantInfoDialogComponent();
    private final CheckOffPromptDialogComponent checkOffPromptDialogComponent = new CheckOffPromptDialogComponent();

    private String currentUsername;

    public WishlistView(
            WishlistController wishlistController,
            GroupController groupController,
            ProfileController profileController,
            AuthenticationContext authenticationContext
    ) {
        this.wishlistController = wishlistController;
        this.groupController = groupController;
        this.profileController = profileController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        wishlistGrid.addColumn(RestaurantDisplay::name).setHeader("Name");
        wishlistGrid.addColumn(RestaurantDisplay::address).setHeader("Address");
        wishlistGrid.addColumn(RestaurantDisplay::groupName).setHeader("Group");
        wishlistGrid.setWidthFull();
        wishlistGrid.getStyle().set("max-width", "720px").set("align-self", "center");
        wishlistGrid.addItemClickListener(event -> openRestaurantInfoDialog(event.getItem()));

        Button addToWishlistButton = new Button("Add to wishlist", event -> openAddToWishlistDialog());
        addToWishlistButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout card = new VerticalLayout(new H1("Wishlist"), addToWishlistButton, wishlistGrid);
        card.setAlignItems(Alignment.CENTER);
        card.setWidthFull();

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Wishlist view requires an authenticated user"));
        refreshWishlist();
    }

    private void refreshWishlist() {
        wishlistGrid.setItems(wishlistController.listWishlistForUser(currentUsername));
    }

    private void openAddToWishlistDialog() {
        List<GroupDisplay> groups = groupController.listGroupsForUser(currentUsername);
        if (groups.isEmpty()) {
            Notification errorNotification = Notification.show("You need to create a group before adding to the wishlist.");
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            getUI().ifPresent(ui -> ui.navigate(RouteConstants.ROUTE_GROUPS));
            return;
        }

        addToWishlistDialogComponent.open(groups, resolveDefaultGroup(groups), this::handleAddToWishlist);
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

    private void handleAddToWishlist(AddRestaurantDisplay addRestaurantDisplay) {
        try {
            wishlistController.addToWishlist(currentUsername, addRestaurantDisplay);
            addToWishlistDialogComponent.close();
            Notification success = Notification.show("Added to wishlist.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshWishlist();
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openRestaurantInfoDialog(RestaurantDisplay restaurantDisplay) {
        restaurantInfoDialogComponent.open(restaurantDisplay, "Check off", () -> openCheckOffPrompt(restaurantDisplay));
    }

    private void openCheckOffPrompt(RestaurantDisplay restaurantDisplay) {
        checkOffPromptDialogComponent.open(
                restaurantDisplay.name(),
                () -> handleCheckOff(restaurantDisplay, true),
                () -> handleCheckOff(restaurantDisplay, false)
        );
    }

    private void handleCheckOff(RestaurantDisplay restaurantDisplay, boolean rateNow) {
        try {
            wishlistController.checkOffWishlistItem(currentUsername, restaurantDisplay.id());
            Notification success = Notification.show("Marked as visited.");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshWishlist();
            if (rateNow) {
                getUI().ifPresent(ui -> ui.navigate(
                        RouteConstants.ROUTE_RESTAURANTS,
                        new QueryParameters(Map.of(RouteConstants.QUERY_PARAM_RATE_RESTAURANT_ID, List.of(String.valueOf(restaurantDisplay.id()))))
                ));
            }
        } catch (ValidationException e) {
            Notification errorNotification = Notification.show(e.getMessage());
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
