package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

@Route(value = RouteConstants.ROUTE_MAIN, layout = MainLayout.class)
@PageTitle("Foodie")
@PermitAll
public class MainView extends VerticalLayout {

    public MainView(AuthenticationContext authenticationContext) {
        setSpacing(true);
        setPadding(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        add(
                new H1("Foodie"),
                new Paragraph("Welcome to the first Vaadin page."),
                new Button("Say hello", event -> Notification.show("Hello from Foodie!")),
                new Button("Log out", event -> authenticationContext.logout())
        );
    }
}
