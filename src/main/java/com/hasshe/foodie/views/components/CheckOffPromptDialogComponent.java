package com.hasshe.foodie.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.util.Assert;

public class CheckOffPromptDialogComponent {

    public interface RateNowListener {
        void onRateNow();
    }

    public interface RateLaterListener {
        void onRateLater();
    }

    private final Dialog dialog = new Dialog();

    private RateNowListener rateNowListener = () -> {};
    private RateLaterListener rateLaterListener = () -> {};

    public CheckOffPromptDialogComponent() {
        dialog.setWidth("420px");
        new DialogCloseButtonComponent(dialog);

        Span message = new Span("Would you like to rate it now, or later from the Restaurants page?");

        Button rateNowButton = new Button("Rate now", event -> {
            dialog.close();
            rateNowListener.onRateNow();
        });
        rateNowButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button rateLaterButton = new Button("Rate later", event -> {
            dialog.close();
            rateLaterListener.onRateLater();
        });
        HorizontalLayout buttons = new HorizontalLayout(rateNowButton, rateLaterButton);
        buttons.setWidthFull();
        buttons.getStyle().set("flex-wrap", "wrap");

        VerticalLayout content = new VerticalLayout(message, buttons);
        content.setPadding(false);
        content.setWidthFull();
        dialog.add(content);
    }

    public void open(String restaurantName, RateNowListener rateNowListener, RateLaterListener rateLaterListener) {
        Assert.hasText(restaurantName, "restaurantName must not be blank");
        Assert.notNull(rateNowListener, "rateNowListener must not be null");
        Assert.notNull(rateLaterListener, "rateLaterListener must not be null");
        this.rateNowListener = rateNowListener;
        this.rateLaterListener = rateLaterListener;
        dialog.setHeaderTitle("Mark \"" + restaurantName + "\" as visited?");
        dialog.open();
    }
}
