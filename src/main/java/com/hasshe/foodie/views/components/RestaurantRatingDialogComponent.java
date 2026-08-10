package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.RestaurantRatingConstants;
import com.hasshe.foodie.dto.RateRestaurantDisplay;
import com.hasshe.foodie.dto.RestaurantRatingDisplay;
import com.hasshe.foodie.dto.RestaurantRatingSummaryDisplay;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.util.Assert;

import java.util.Locale;

public class RestaurantRatingDialogComponent {

    public interface SubmitListener {
        void onSubmit(RateRestaurantDisplay rateRestaurantDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final Span overallAverageValue = new Span();
    private final Span ratingCountValue = new Span();
    private final VerticalLayout categoryAveragesLayout = new VerticalLayout();
    private final RatingSliderComponent foodSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_FOOD, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent serviceSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_SERVICE, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent vibeSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_VIBE, RestaurantRatingConstants.DEFAULT_SCORE);

    private SubmitListener submitListener = rateRestaurantDisplay -> {};

    public RestaurantRatingDialogComponent() {
        dialog.setWidth("360px");
        new DialogCloseButtonComponent(dialog);

        HorizontalLayout overallRow = new HorizontalLayout(new Span("Overall average"), overallAverageValue);
        overallRow.setWidthFull();
        overallRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout countRow = new HorizontalLayout(new Span("Ratings submitted"), ratingCountValue);
        countRow.setWidthFull();
        countRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        categoryAveragesLayout.setPadding(false);
        categoryAveragesLayout.setSpacing(false);
        categoryAveragesLayout.setWidthFull();

        Button saveButton = new Button("Save rating", event -> handleSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttons = new HorizontalLayout(saveButton);
        buttons.setWidthFull();
        buttons.getStyle().set("flex-wrap", "wrap");

        VerticalLayout content = new VerticalLayout(
                overallRow,
                countRow,
                categoryAveragesLayout,
                new Hr(),
                new Span("Your rating"),
                foodSlider,
                serviceSlider,
                vibeSlider,
                buttons
        );
        content.setPadding(false);
        content.setWidthFull();
        dialog.add(content);
    }

    public void open(RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay, SubmitListener submitListener) {
        Assert.notNull(restaurantRatingSummaryDisplay, "restaurantRatingSummaryDisplay must not be null");
        Assert.notNull(submitListener, "submitListener must not be null");
        this.submitListener = submitListener;
        dialog.setHeaderTitle(restaurantRatingSummaryDisplay.restaurantName());
        refresh(restaurantRatingSummaryDisplay);
        dialog.open();
    }

    public void close() {
        dialog.close();
    }

    public void refresh(RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay) {
        Assert.notNull(restaurantRatingSummaryDisplay, "restaurantRatingSummaryDisplay must not be null");

        overallAverageValue.setText(formatScore(restaurantRatingSummaryDisplay.overallAverage()));
        ratingCountValue.setText(String.valueOf(restaurantRatingSummaryDisplay.ratingCount()));

        categoryAveragesLayout.removeAll();
        categoryAveragesLayout.add(
                averageRow(RestaurantRatingConstants.CATEGORY_FOOD, restaurantRatingSummaryDisplay.averageFood()),
                averageRow(RestaurantRatingConstants.CATEGORY_SERVICE, restaurantRatingSummaryDisplay.averageService()),
                averageRow(RestaurantRatingConstants.CATEGORY_VIBE, restaurantRatingSummaryDisplay.averageVibe())
        );

        RestaurantRatingDisplay currentUserRating = restaurantRatingSummaryDisplay.currentUserRating();
        foodSlider.setValue(currentUserRating != null ? currentUserRating.food() : RestaurantRatingConstants.DEFAULT_SCORE);
        serviceSlider.setValue(currentUserRating != null ? currentUserRating.service() : RestaurantRatingConstants.DEFAULT_SCORE);
        vibeSlider.setValue(currentUserRating != null ? currentUserRating.vibe() : RestaurantRatingConstants.DEFAULT_SCORE);
    }

    private Component averageRow(String categoryLabel, double average) {
        HorizontalLayout row = new HorizontalLayout(new Span(categoryLabel + " group average"), new Span(formatScore(average)));
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return row;
    }

    private String formatScore(double score) {
        return String.format(Locale.US, "%.1f", score);
    }

    private void handleSave() {
        RateRestaurantDisplay rateRestaurantDisplay = new RateRestaurantDisplay(
                foodSlider.getValue(),
                serviceSlider.getValue(),
                vibeSlider.getValue()
        );
        submitListener.onSubmit(rateRestaurantDisplay);
    }
}
