package com.hasshe.foodie.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Foodie")
public class MainView extends VerticalLayout {

    public MainView() {
        setSpacing(true);
        setPadding(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        add(
                new H1("Foodie"),
                new Paragraph("Welcome to the first Vaadin page."),
                new Button("Say hello", event -> Notification.show("Hello from Foodie!"))
        );
    }
}
