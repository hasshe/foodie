package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.controller.ProfileController;
import com.hasshe.foodie.dto.ChangePasswordDisplay;
import com.hasshe.foodie.dto.UpdateProfileDisplay;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.views.components.NotificationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@Route(value = RouteConstants.ROUTE_PROFILE, layout = MainLayout.class)
@PageTitle("Profile | Foodie")
@PermitAll
public class ProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final ProfileController profileController;
    private final AuthenticationContext authenticationContext;

    private final TextField usernameField = new TextField("Username");
    private final TextField displayNameField = new TextField("Display name");
    private final Select<UserIconDisplay> iconSelect = new Select<>();

    private final PasswordField currentPasswordField = new PasswordField("Current password");
    private final PasswordField newPasswordField = new PasswordField("New password");
    private final PasswordField confirmNewPasswordField = new PasswordField("Confirm new password");
    private final NotificationComponent notificationComponent = new NotificationComponent();

    private String currentUsername;

    public ProfileView(ProfileController profileController, AuthenticationContext authenticationContext) {
        this.profileController = profileController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        usernameField.setRequiredIndicatorVisible(true);
        usernameField.setMaxLength(UserConstants.USERNAME_MAX_LENGTH);
        usernameField.setWidthFull();

        displayNameField.setRequiredIndicatorVisible(true);
        displayNameField.setMaxLength(UserConstants.DISPLAY_NAME_MAX_LENGTH);
        displayNameField.setWidthFull();

        iconSelect.setLabel("Icon");
        iconSelect.setWidthFull();
        iconSelect.setRenderer(new ComponentRenderer<>(this::renderIconOption));
        iconSelect.setItemLabelGenerator(this::generateIconLabel);
        iconSelect.setEmptySelectionAllowed(true);
        iconSelect.setEmptySelectionCaption("No icon");

        Button saveProfileButton = new Button("Save changes", event -> saveProfile());
        saveProfileButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveProfileButton.setWidthFull();

        VerticalLayout profileSection = new VerticalLayout(
                new H2("Account"),
                usernameField,
                displayNameField,
                iconSelect,
                saveProfileButton
        );
        profileSection.setPadding(false);
        profileSection.setWidth("320px");

        currentPasswordField.setRequiredIndicatorVisible(true);
        currentPasswordField.setWidthFull();

        newPasswordField.setRequiredIndicatorVisible(true);
        newPasswordField.setMaxLength(UserConstants.PASSWORD_MAX_LENGTH);
        newPasswordField.setWidthFull();

        confirmNewPasswordField.setRequiredIndicatorVisible(true);
        confirmNewPasswordField.setMaxLength(UserConstants.PASSWORD_MAX_LENGTH);
        confirmNewPasswordField.setWidthFull();

        Button changePasswordButton = new Button("Change password", event -> changePassword());
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordButton.setWidthFull();

        VerticalLayout passwordSection = new VerticalLayout(
                new H2("Password"),
                currentPasswordField,
                newPasswordField,
                confirmNewPasswordField,
                changePasswordButton
        );
        passwordSection.setPadding(false);
        passwordSection.setWidth("320px");

        VerticalLayout card = new VerticalLayout(new H1("Profile"), profileSection, passwordSection);
        card.setAlignItems(Alignment.CENTER);
        card.setWidth("320px");

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Profile view requires an authenticated user"));

        List<UserIconDisplay> availableIcons = profileController.listAvailableIcons();
        iconSelect.setItems(availableIcons);

        profileController.getProfile(currentUsername).ifPresent(profile -> {
            usernameField.setValue(profile.username());
            displayNameField.setValue(profile.displayName());
            if (profile.userIcon() != null) {
                iconSelect.setValue(profile.userIcon());
            }
        });
    }

    private Span renderIconOption(UserIconDisplay userIconDisplay) {
        if (userIconDisplay == null) {
            return new Span("No icon");
        }
        return new Span(VaadinIcon.valueOf(userIconDisplay.iconKey()).create(), new Span(" " + userIconDisplay.label()));
    }

    private String generateIconLabel(UserIconDisplay userIconDisplay) {
        return userIconDisplay == null ? "No icon" : userIconDisplay.label();
    }

    private void saveProfile() {
        if (usernameField.isEmpty() || displayNameField.isEmpty()) {
            notificationComponent.showInfo("Please fill in all fields.");
            return;
        }

        boolean usernameChanged = !usernameField.getValue().equals(currentUsername);
        Long iconId = iconSelect.getValue() == null ? null : iconSelect.getValue().id();

        try {
            profileController.updateProfile(currentUsername, new UpdateProfileDisplay(
                    usernameField.getValue(),
                    displayNameField.getValue(),
                    iconId
            ));

            if (usernameChanged) {
                notificationComponent.showInfo("Username changed. Please log in again.");
                authenticationContext.logout();
            } else {
                notificationComponent.showSuccess("Profile updated.");
                findMainLayout().ifPresent(MainLayout::refreshProfileIcon);
            }
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }

    private Optional<MainLayout> findMainLayout() {
        Optional<Component> ancestor = getParent();
        while (ancestor.isPresent()) {
            Component component = ancestor.get();
            if (component instanceof MainLayout mainLayout) {
                return Optional.of(mainLayout);
            }
            ancestor = component.getParent();
        }
        return Optional.empty();
    }

    private void changePassword() {
        if (currentPasswordField.isEmpty() || newPasswordField.isEmpty() || confirmNewPasswordField.isEmpty()) {
            notificationComponent.showInfo("Please fill in all password fields.");
            return;
        }
        if (!newPasswordField.getValue().equals(confirmNewPasswordField.getValue())) {
            notificationComponent.showInfo("New passwords do not match.");
            return;
        }
        if (newPasswordField.getValue().length() < UserConstants.PASSWORD_MIN_LENGTH) {
            notificationComponent.showInfo("Password must be at least " + UserConstants.PASSWORD_MIN_LENGTH + " characters.");
            return;
        }

        try {
            profileController.changePassword(currentUsername, new ChangePasswordDisplay(
                    currentPasswordField.getValue(),
                    newPasswordField.getValue()
            ));
            notificationComponent.showInfo("Password changed. Please log in again.");
            authenticationContext.logout();
        } catch (ValidationException e) {
            notificationComponent.showError(e.getMessage());
        }
    }
}
