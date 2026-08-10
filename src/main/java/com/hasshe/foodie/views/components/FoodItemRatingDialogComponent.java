package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.FoodItemRatingConstants;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
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

public class FoodItemRatingDialogComponent {

    public interface SubmitListener {
        void onSubmit(RateFoodItemDisplay rateFoodItemDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final Span overallAverageValue = new Span();
    private final Span ratingCountValue = new Span();
    private final RatingSliderComponent ratingSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_RATING, FoodItemRatingConstants.DEFAULT_SCORE);

    private SubmitListener submitListener = rateFoodItemDisplay -> {};

    public FoodItemRatingDialogComponent() {
        dialog.setWidth("320px");

        HorizontalLayout overallRow = new HorizontalLayout(new Span("Overall average"), overallAverageValue);
        overallRow.setWidthFull();
        overallRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout countRow = new HorizontalLayout(new Span("Ratings submitted"), ratingCountValue);
        countRow.setWidthFull();
        countRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button saveButton = new Button("Save rating", event -> handleSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button closeButton = new Button("Close", event -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(saveButton, closeButton);
        buttons.setWidthFull();
        buttons.getStyle().set("flex-wrap", "wrap");

        VerticalLayout content = new VerticalLayout(
                overallRow,
                countRow,
                new Hr(),
                new Span("Your rating"),
                ratingSlider,
                buttons
        );
        content.setPadding(false);
        content.setWidthFull();
        dialog.add(content);
    }

    public void open(FoodItemRatingSummaryDisplay foodItemRatingSummaryDisplay, SubmitListener submitListener) {
        Assert.notNull(foodItemRatingSummaryDisplay, "foodItemRatingSummaryDisplay must not be null");
        Assert.notNull(submitListener, "submitListener must not be null");
        this.submitListener = submitListener;
        dialog.setHeaderTitle(foodItemRatingSummaryDisplay.foodItemName());
        refresh(foodItemRatingSummaryDisplay);
        dialog.open();
    }

    public void close() {
        dialog.close();
    }

    public void refresh(FoodItemRatingSummaryDisplay foodItemRatingSummaryDisplay) {
        Assert.notNull(foodItemRatingSummaryDisplay, "foodItemRatingSummaryDisplay must not be null");

        overallAverageValue.setText(formatScore(foodItemRatingSummaryDisplay.averageRating()));
        ratingCountValue.setText(String.valueOf(foodItemRatingSummaryDisplay.ratingCount()));

        FoodItemRatingDisplay currentUserRating = foodItemRatingSummaryDisplay.currentUserRating();
        ratingSlider.setValue(currentUserRating != null ? currentUserRating.rating() : FoodItemRatingConstants.DEFAULT_SCORE);
    }

    private String formatScore(double score) {
        return String.format(Locale.US, "%.1f", score);
    }

    private void handleSave() {
        RateFoodItemDisplay rateFoodItemDisplay = new RateFoodItemDisplay(ratingSlider.getValue());
        submitListener.onSubmit(rateFoodItemDisplay);
    }
}
