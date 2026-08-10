package com.hasshe.foodie.views.components;

import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.views.MainView;
import com.hasshe.foodie.views.ProfileView;
import com.hasshe.foodie.views.RestaurantsView;
import com.hasshe.foodie.views.WishlistView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class FooterMenuComponent extends HorizontalLayout {

    private final Span profileIconHolder = new Span();

    public FooterMenuComponent(UserIconDisplay initialProfileIcon) {
        setWidthFull();
        setPadding(true);
        setSpacing(false);
        setJustifyContentMode(JustifyContentMode.AROUND);

        setProfileIcon(initialProfileIcon);

        add(
                createNavItem(VaadinIcon.HOME, "Home", MainView.class),
                createNavItem(VaadinIcon.CUTLERY, "Restaurants", RestaurantsView.class),
                createNavItem(VaadinIcon.STAR, "Wishlist", WishlistView.class),
                createProfileNavItem()
        );
    }

    public void setProfileIcon(UserIconDisplay userIconDisplay) {
        profileIconHolder.removeAll();
        VaadinIcon icon = userIconDisplay == null ? VaadinIcon.USER : VaadinIcon.valueOf(userIconDisplay.iconKey());
        profileIconHolder.add(icon.create());
    }

    private Component createProfileNavItem() {
        RouterLink link = new RouterLink("Profile", ProfileView.class);
        link.removeAll();
        link.getStyle().set("text-decoration", "none").set("color", "inherit");

        VerticalLayout itemLayout = new VerticalLayout(profileIconHolder, new Span("Profile"));
        itemLayout.setPadding(false);
        itemLayout.setSpacing(false);
        itemLayout.setAlignItems(Alignment.CENTER);

        link.add(itemLayout);
        return link;
    }

    private Component createNavItem(VaadinIcon icon, String label, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(label, navigationTarget);
        link.removeAll();
        link.getStyle().set("text-decoration", "none").set("color", "inherit");

        VerticalLayout itemLayout = new VerticalLayout(icon.create(), new Span(label));
        itemLayout.setPadding(false);
        itemLayout.setSpacing(false);
        itemLayout.setAlignItems(Alignment.CENTER);

        link.add(itemLayout);
        return link;
    }
}
