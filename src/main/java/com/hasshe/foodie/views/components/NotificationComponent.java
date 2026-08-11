package com.hasshe.foodie.views.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.util.Assert;

public class NotificationComponent {

    private static final int DURATION_MILLIS = 5000;
    private static final Position POSITION = Position.TOP_CENTER;

    public void showInfo(String message) {
        Assert.hasText(message, "message must not be blank");
        Notification.show(message, DURATION_MILLIS, POSITION);
    }

    public void showSuccess(String message) {
        Assert.hasText(message, "message must not be blank");
        Notification notification = Notification.show(message, DURATION_MILLIS, POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public void showError(String message) {
        Assert.hasText(message, "message must not be blank");
        Notification notification = Notification.show(message, DURATION_MILLIS, POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
