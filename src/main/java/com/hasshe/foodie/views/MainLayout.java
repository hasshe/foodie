package com.hasshe.foodie.views;

import com.hasshe.foodie.controller.ProfileController;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.hasshe.foodie.views.components.FooterMenuComponent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;

public class MainLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

    private final ProfileController profileController;
    private final AuthenticationContext authenticationContext;

    private final Div content = new Div();
    private final FooterMenuComponent footerMenuComponent;

    public MainLayout(ProfileController profileController, AuthenticationContext authenticationContext) {
        this.profileController = profileController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        content.setSizeFull();
        content.getStyle().set("overflow-y", "auto");
        setFlexGrow(1, content);

        footerMenuComponent = new FooterMenuComponent(currentProfileIcon());
        add(content, footerMenuComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        refreshProfileIcon();
    }

    public void refreshProfileIcon() {
        footerMenuComponent.setProfileIcon(currentProfileIcon());
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.getElement().appendChild(content.getElement());
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        content.removeAll();
    }

    private UserIconDisplay currentProfileIcon() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .flatMap(profileController::getProfile)
                .map(UserProfileDisplay::userIcon)
                .orElse(null);
    }
}
