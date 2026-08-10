package com.hasshe.foodie.views.components;

import com.hasshe.foodie.dto.RestaurantDisplay;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.util.Assert;

public class RestaurantInfoDialogComponent {

    public interface ActionListener {
        void onAction();
    }

    private final Dialog dialog = new Dialog();
    private final VerticalLayout infoLayout = new VerticalLayout();
    private final Button actionButton = new Button();
    private final Button secondaryActionButton = new Button();

    private ActionListener actionListener = () -> {};
    private ActionListener secondaryActionListener = () -> {};

    public RestaurantInfoDialogComponent() {
        dialog.setWidth("360px");
        new DialogCloseButtonComponent(dialog);

        infoLayout.setPadding(false);
        infoLayout.setSpacing(false);
        infoLayout.setWidthFull();

        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionButton.addClickListener(event -> {
            dialog.close();
            actionListener.onAction();
        });
        secondaryActionButton.addClickListener(event -> {
            dialog.close();
            secondaryActionListener.onAction();
        });
        HorizontalLayout buttons = new HorizontalLayout(actionButton, secondaryActionButton);
        buttons.setWidthFull();
        buttons.getStyle().set("flex-wrap", "wrap");

        VerticalLayout content = new VerticalLayout(infoLayout, buttons);
        content.setPadding(false);
        content.setWidthFull();
        dialog.add(content);
    }

    public void open(RestaurantDisplay restaurantDisplay, String actionButtonLabel, ActionListener actionListener) {
        openInternal(restaurantDisplay, actionButtonLabel, actionListener, null, null);
    }

    public void open(
            RestaurantDisplay restaurantDisplay,
            String actionButtonLabel,
            ActionListener actionListener,
            String secondaryActionButtonLabel,
            ActionListener secondaryActionListener
    ) {
        Assert.hasText(secondaryActionButtonLabel, "secondaryActionButtonLabel must not be blank");
        Assert.notNull(secondaryActionListener, "secondaryActionListener must not be null");
        openInternal(restaurantDisplay, actionButtonLabel, actionListener, secondaryActionButtonLabel, secondaryActionListener);
    }

    private void openInternal(
            RestaurantDisplay restaurantDisplay,
            String actionButtonLabel,
            ActionListener actionListener,
            String secondaryActionButtonLabel,
            ActionListener secondaryActionListener
    ) {
        Assert.notNull(restaurantDisplay, "restaurantDisplay must not be null");
        Assert.hasText(actionButtonLabel, "actionButtonLabel must not be blank");
        Assert.notNull(actionListener, "actionListener must not be null");
        this.actionListener = actionListener;
        actionButton.setText(actionButtonLabel);

        boolean hasSecondaryAction = secondaryActionButtonLabel != null;
        this.secondaryActionListener = hasSecondaryAction ? secondaryActionListener : () -> {};
        secondaryActionButton.setText(hasSecondaryAction ? secondaryActionButtonLabel : "");
        secondaryActionButton.setVisible(hasSecondaryAction);

        dialog.setHeaderTitle(restaurantDisplay.name());
        infoLayout.removeAll();
        infoLayout.add(infoRow("Address", restaurantDisplay.address()));
        if (restaurantDisplay.cuisineType() != null) {
            infoLayout.add(infoRow("Cuisine", restaurantDisplay.cuisineType()));
        }
        if (restaurantDisplay.website() != null) {
            infoLayout.add(infoRow("Website", restaurantDisplay.website()));
        }
        infoLayout.add(infoRow("Group", restaurantDisplay.groupName()));

        dialog.open();
    }

    public void close() {
        dialog.close();
    }

    private Component infoRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout(new Span(label), new Span(value));
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return row;
    }
}
