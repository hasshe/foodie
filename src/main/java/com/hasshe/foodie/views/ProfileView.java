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

@Route(value = RouteConstants.ROUTE_PROFILE, layout = MainLayout.class)
@PageTitle("Profile | Foodie")
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Profile"), new Paragraph("Coming soon."));
    }
}
