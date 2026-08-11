package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.RestaurantConstants;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.util.Assert;

import java.util.List;

public class AddRestaurantDialogComponent {

    public interface SubmitListener {
        void onSubmit(AddRestaurantDisplay addRestaurantDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final TextField nameField = new TextField("Name");
    private final TextField addressField = new TextField("Address");
    private final TextField cuisineTypeField = new TextField("Cuisine type");
    private final TextField websiteField = new TextField("Website");
    private final Select<GroupDisplay> groupSelect = new Select<>();
    private final NotificationComponent notificationComponent = new NotificationComponent();

    private SubmitListener submitListener = addRestaurantDisplay -> {};

    public AddRestaurantDialogComponent(String headerTitle) {
        Assert.hasText(headerTitle, "headerTitle must not be blank");
        dialog.setHeaderTitle(headerTitle);
        new DialogCloseButtonComponent(dialog);

        nameField.setRequiredIndicatorVisible(true);
        nameField.setMaxLength(RestaurantConstants.NAME_MAX_LENGTH);
        nameField.setWidthFull();

        addressField.setRequiredIndicatorVisible(true);
        addressField.setMaxLength(RestaurantConstants.ADDRESS_MAX_LENGTH);
        addressField.setWidthFull();

        cuisineTypeField.setMaxLength(RestaurantConstants.CUISINE_TYPE_MAX_LENGTH);
        cuisineTypeField.setWidthFull();

        websiteField.setMaxLength(RestaurantConstants.WEBSITE_MAX_LENGTH);
        websiteField.setWidthFull();

        groupSelect.setLabel("Group");
        groupSelect.setRequiredIndicatorVisible(true);
        groupSelect.setWidthFull();
        groupSelect.setItemLabelGenerator(this::generateGroupLabel);

        VerticalLayout formLayout = new VerticalLayout(
                nameField, addressField, cuisineTypeField, websiteField, groupSelect
        );
        formLayout.setPadding(false);
        formLayout.setWidth("320px");

        Button saveButton = new Button("Add", event -> handleSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttons);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);
    }

    public void open(List<GroupDisplay> groups, GroupDisplay defaultGroup, SubmitListener submitListener) {
        Assert.notNull(groups, "groups must not be null");
        Assert.notNull(submitListener, "submitListener must not be null");
        this.submitListener = submitListener;

        nameField.clear();
        addressField.clear();
        cuisineTypeField.clear();
        websiteField.clear();

        groupSelect.setItems(groups);
        groupSelect.setValue(defaultGroup);

        dialog.open();
    }

    public void close() {
        dialog.close();
    }

    private void handleSave() {
        if (nameField.isEmpty() || addressField.isEmpty() || groupSelect.isEmpty()) {
            notificationComponent.showInfo("Please fill in the required fields.");
            return;
        }

        AddRestaurantDisplay addRestaurantDisplay = new AddRestaurantDisplay(
                nameField.getValue(),
                addressField.getValue(),
                blankToNull(cuisineTypeField.getValue()),
                blankToNull(websiteField.getValue()),
                null,
                groupSelect.getValue().id()
        );
        submitListener.onSubmit(addRestaurantDisplay);
    }

    private String generateGroupLabel(GroupDisplay groupDisplay) {
        return groupDisplay == null ? "" : groupDisplay.name();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
