package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(RouteConstants.ROUTE_LOGIN)
@PageTitle("Login | Foodie")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.setAction(RouteConstants.ROUTE_LOGIN);

        add(new H1("Foodie"), loginForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean hasError = event.getLocation().getQueryParameters()
                .getParameters()
                .containsKey(RouteConstants.QUERY_PARAM_ERROR);
        loginForm.setError(hasError);
    }
}
