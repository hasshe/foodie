package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.FoodItemRatingConstants;
import com.hasshe.foodie.dto.FoodItemRatingDisplay;
import com.hasshe.foodie.dto.FoodItemRatingSummaryDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
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

public class FoodItemRatingDialogComponent {

    public interface SubmitListener {
        void onSubmit(RateFoodItemDisplay rateFoodItemDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final Span overallAverageValue = new Span();
    private final Span ratingCountValue = new Span();
    private final VerticalLayout categoryAveragesLayout = new VerticalLayout();
    private final RatingSliderComponent tasteSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_TASTE, FoodItemRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent presentationSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_PRESENTATION, FoodItemRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent portionQualitySlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_PORTION_QUALITY, FoodItemRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent valueForPriceSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_VALUE_FOR_PRICE, FoodItemRatingConstants.DEFAULT_SCORE);

    private SubmitListener submitListener = rateFoodItemDisplay -> {};

    public FoodItemRatingDialogComponent() {
        dialog.setWidth("420px");

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
        Button closeButton = new Button("Close", event -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(saveButton, closeButton);

        VerticalLayout content = new VerticalLayout(
                overallRow,
                countRow,
                categoryAveragesLayout,
                new Hr(),
                new Span("Your rating"),
                tasteSlider,
                presentationSlider,
                portionQualitySlider,
                valueForPriceSlider,
                buttons
        );
        content.setPadding(false);
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

        overallAverageValue.setText(formatScore(foodItemRatingSummaryDisplay.overallAverage()));
        ratingCountValue.setText(String.valueOf(foodItemRatingSummaryDisplay.ratingCount()));

        categoryAveragesLayout.removeAll();
        categoryAveragesLayout.add(
                averageRow(FoodItemRatingConstants.CATEGORY_TASTE, foodItemRatingSummaryDisplay.averageTaste()),
                averageRow(FoodItemRatingConstants.CATEGORY_PRESENTATION, foodItemRatingSummaryDisplay.averagePresentation()),
                averageRow(FoodItemRatingConstants.CATEGORY_PORTION_QUALITY, foodItemRatingSummaryDisplay.averagePortionQuality()),
                averageRow(FoodItemRatingConstants.CATEGORY_VALUE_FOR_PRICE, foodItemRatingSummaryDisplay.averageValueForPrice())
        );

        FoodItemRatingDisplay currentUserRating = foodItemRatingSummaryDisplay.currentUserRating();
        tasteSlider.setValue(currentUserRating != null ? currentUserRating.taste() : FoodItemRatingConstants.DEFAULT_SCORE);
        presentationSlider.setValue(currentUserRating != null ? currentUserRating.presentation() : FoodItemRatingConstants.DEFAULT_SCORE);
        portionQualitySlider.setValue(currentUserRating != null ? currentUserRating.portionQuality() : FoodItemRatingConstants.DEFAULT_SCORE);
        valueForPriceSlider.setValue(currentUserRating != null ? currentUserRating.valueForPrice() : FoodItemRatingConstants.DEFAULT_SCORE);
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
        RateFoodItemDisplay rateFoodItemDisplay = new RateFoodItemDisplay(
                tasteSlider.getValue(),
                presentationSlider.getValue(),
                portionQualitySlider.getValue(),
                valueForPriceSlider.getValue()
        );
        submitListener.onSubmit(rateFoodItemDisplay);
    }
}
