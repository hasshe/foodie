package com.hasshe.foodie.views;

import com.hasshe.foodie.constants.GroupConstants;
import com.hasshe.foodie.constants.RouteConstants;
import com.hasshe.foodie.controller.GroupController;
import com.hasshe.foodie.controller.ProfileController;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Route(value = RouteConstants.ROUTE_GROUPS, layout = MainLayout.class)
@PageTitle("Groups | Foodie")
@PermitAll
public class GroupsView extends VerticalLayout implements BeforeEnterObserver {

    private final GroupController groupController;
    private final ProfileController profileController;
    private final AuthenticationContext authenticationContext;

    private final TextField groupNameField = new TextField("Group name");
    private final VerticalLayout groupListLayout = new VerticalLayout();

    private String currentUsername;
    private Long defaultGroupId;

    public GroupsView(GroupController groupController, ProfileController profileController, AuthenticationContext authenticationContext) {
        this.groupController = groupController;
        this.profileController = profileController;
        this.authenticationContext = authenticationContext;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        groupNameField.setRequiredIndicatorVisible(true);
        groupNameField.setMaxLength(GroupConstants.NAME_MAX_LENGTH);
        groupNameField.setWidthFull();

        Button createGroupButton = new Button("Create group", event -> createGroup());
        createGroupButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createGroupButton.setWidthFull();

        VerticalLayout createSection = new VerticalLayout(new H2("Create a group"), groupNameField, createGroupButton);
        createSection.setPadding(false);
        createSection.setWidth("320px");

        groupListLayout.setPadding(false);
        groupListLayout.setWidthFull();

        VerticalLayout listSection = new VerticalLayout(new H2("Your groups"), groupListLayout);
        listSection.setPadding(false);
        listSection.setWidth("320px");

        VerticalLayout card = new VerticalLayout(new H1("Groups"), createSection, listSection);
        card.setAlignItems(Alignment.CENTER);
        card.setWidth("320px");

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        currentUsername = authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Groups view requires an authenticated user"));
        refreshGroups();
    }

    private void refreshGroups() {
        defaultGroupId = profileController.getProfile(currentUsername)
                .map(UserProfileDisplay::defaultGroup)
                .map(GroupDisplay::id)
                .orElse(null);

        List<GroupDisplay> groups = groupController.listGroupsForUser(currentUsername);
        groupListLayout.removeAll();
        if (groups.isEmpty()) {
            groupListLayout.add(new Span("You don't have any groups yet."));
            return;
        }
        for (GroupDisplay group : groups) {
            groupListLayout.add(createGroupRow(group));
        }
    }

    private Component createGroupRow(GroupDisplay group) {
        boolean isDefault = group.id().equals(defaultGroupId);

        Span nameSpan = new Span(group.name());
        nameSpan.getStyle().set("font-weight", "600");

        HorizontalLayout row = new HorizontalLayout(nameSpan);
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);

        if (isDefault) {
            Span defaultBadge = new Span("Default");
            defaultBadge.getStyle()
                    .set("color", "var(--lumo-primary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            row.add(defaultBadge);
        } else {
            Button setDefaultButton = new Button("Set as default", event -> setDefaultGroup(group.id()));
            row.add(setDefaultButton);
        }

        return row;
    }

    private void createGroup() {
        if (groupNameField.isEmpty()) {
            Notification.show("Please enter a group name.");
            return;
        }

        groupController.createGroup(currentUsername, new CreateGroupDisplay(groupNameField.getValue()));
        groupNameField.clear();
        Notification success = Notification.show("Group created.");
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        refreshGroups();
    }

    private void setDefaultGroup(Long groupId) {
        profileController.setDefaultGroup(currentUsername, groupId);
        Notification success = Notification.show("Default group updated.");
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        refreshGroups();
    }
}
