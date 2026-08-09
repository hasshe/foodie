package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = RouteConstants.ROUTE_WISHLIST, layout = MainLayout.class)
@PageTitle("Wishlist | Foodie")
@PermitAll
public class WishlistView extends VerticalLayout {

    public WishlistView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Wishlist"), new Paragraph("Coming soon."));
    }
}
