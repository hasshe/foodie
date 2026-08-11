package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.controller.RegistrationController;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.views.components.NotificationComponent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(RouteConstants.ROUTE_REGISTER)
@PageTitle("Register | Foodie")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final RegistrationController registrationController;

    private final TextField usernameField = new TextField("Username");
    private final TextField displayNameField = new TextField("Display name");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm password");
    private final NotificationComponent notificationComponent = new NotificationComponent();

    public RegisterView(RegistrationController registrationController) {
        this.registrationController = registrationController;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        usernameField.setRequiredIndicatorVisible(true);
        usernameField.setMaxLength(UserConstants.USERNAME_MAX_LENGTH);
        usernameField.setWidthFull();
        usernameField.setTooltipText("The name you'll use to log in. Not shown to other users.");

        displayNameField.setRequiredIndicatorVisible(true);
        displayNameField.setMaxLength(UserConstants.DISPLAY_NAME_MAX_LENGTH);
        displayNameField.setWidthFull();
        displayNameField.setTooltipText("How other users will see you across the app.");

        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setMaxLength(UserConstants.PASSWORD_MAX_LENGTH);
        passwordField.setWidthFull();

        confirmPasswordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setMaxLength(UserConstants.PASSWORD_MAX_LENGTH);
        confirmPasswordField.setWidthFull();

        Button registerButton = new Button("Register", event -> attemptRegistration());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClickShortcut(Key.ENTER);

        Anchor loginLink = new Anchor(RouteConstants.ROUTE_LOGIN, "Already have an account? Log in");

        VerticalLayout card = new VerticalLayout(
                new H1("Create Account"),
                usernameField,
                displayNameField,
                passwordField,
                confirmPasswordField,
                registerButton,
                loginLink
        );
        card.setWidth("320px");
        card.setAlignItems(Alignment.CENTER);

        add(card);
    }

    private void attemptRegistration() {
        if (hasBlankRequiredField()) {
            notificationComponent.showInfo("Please fill in all fields.");
            return;
        }
        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            notificationComponent.showInfo("Passwords do not match.");
            return;
        }
        if (passwordField.getValue().length() < UserConstants.PASSWORD_MIN_LENGTH) {
            notificationComponent.showInfo("Password must be at least " + UserConstants.PASSWORD_MIN_LENGTH + " characters.");
            return;
        }

        try {
            registrationController.registerUser(new RegisterUserDisplay(
                    usernameField.getValue(),
                    passwordField.getValue(),
                    displayNameField.getValue()
            ));
            notificationComponent.showSuccess("Registration successful. Please log in.");
            getUI().ifPresent(ui -> ui.navigate(RouteConstants.ROUTE_LOGIN));
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private boolean hasBlankRequiredField() {
        return usernameField.isEmpty() || displayNameField.isEmpty()
                || passwordField.isEmpty() || confirmPasswordField.isEmpty();
    }
}
