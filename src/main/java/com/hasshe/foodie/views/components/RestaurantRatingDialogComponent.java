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
    private final RatingSliderComponent employeesServiceSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_EMPLOYEES_SERVICE, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent audioMusicSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_AUDIO_MUSIC, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent generalVibesSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_GENERAL_VIBES, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent priceForQualitySlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_PRICE_FOR_QUALITY, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent locationLocaleSlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_LOCATION_LOCALE, RestaurantRatingConstants.DEFAULT_SCORE);
    private final RatingSliderComponent foodQualitySlider =
            new RatingSliderComponent(RestaurantRatingConstants.CATEGORY_FOOD_QUALITY, RestaurantRatingConstants.DEFAULT_SCORE);

    private SubmitListener submitListener = rateRestaurantDisplay -> {};
    private Runnable foodItemsRequestedListener = () -> {};

    public RestaurantRatingDialogComponent() {
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
        Button foodItemsButton = new Button("Food items", event -> foodItemsRequestedListener.run());
        Button closeButton = new Button("Close", event -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(saveButton, foodItemsButton, closeButton);

        VerticalLayout content = new VerticalLayout(
                overallRow,
                countRow,
                categoryAveragesLayout,
                new Hr(),
                new Span("Your rating"),
                employeesServiceSlider,
                audioMusicSlider,
                generalVibesSlider,
                priceForQualitySlider,
                locationLocaleSlider,
                foodQualitySlider,
                buttons
        );
        content.setPadding(false);
        dialog.add(content);
    }

    public void open(RestaurantRatingSummaryDisplay restaurantRatingSummaryDisplay, SubmitListener submitListener, Runnable foodItemsRequestedListener) {
        Assert.notNull(restaurantRatingSummaryDisplay, "restaurantRatingSummaryDisplay must not be null");
        Assert.notNull(submitListener, "submitListener must not be null");
        Assert.notNull(foodItemsRequestedListener, "foodItemsRequestedListener must not be null");
        this.submitListener = submitListener;
        this.foodItemsRequestedListener = foodItemsRequestedListener;
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
                averageRow(RestaurantRatingConstants.CATEGORY_EMPLOYEES_SERVICE, restaurantRatingSummaryDisplay.averageEmployeesService()),
                averageRow(RestaurantRatingConstants.CATEGORY_AUDIO_MUSIC, restaurantRatingSummaryDisplay.averageAudioMusic()),
                averageRow(RestaurantRatingConstants.CATEGORY_GENERAL_VIBES, restaurantRatingSummaryDisplay.averageGeneralVibes()),
                averageRow(RestaurantRatingConstants.CATEGORY_PRICE_FOR_QUALITY, restaurantRatingSummaryDisplay.averagePriceForQuality()),
                averageRow(RestaurantRatingConstants.CATEGORY_LOCATION_LOCALE, restaurantRatingSummaryDisplay.averageLocationLocale()),
                averageRow(RestaurantRatingConstants.CATEGORY_FOOD_QUALITY, restaurantRatingSummaryDisplay.averageFoodQuality())
        );

        RestaurantRatingDisplay currentUserRating = restaurantRatingSummaryDisplay.currentUserRating();
        employeesServiceSlider.setValue(currentUserRating != null ? currentUserRating.employeesService() : RestaurantRatingConstants.DEFAULT_SCORE);
        audioMusicSlider.setValue(currentUserRating != null ? currentUserRating.audioMusic() : RestaurantRatingConstants.DEFAULT_SCORE);
        generalVibesSlider.setValue(currentUserRating != null ? currentUserRating.generalVibes() : RestaurantRatingConstants.DEFAULT_SCORE);
        priceForQualitySlider.setValue(currentUserRating != null ? currentUserRating.priceForQuality() : RestaurantRatingConstants.DEFAULT_SCORE);
        locationLocaleSlider.setValue(currentUserRating != null ? currentUserRating.locationLocale() : RestaurantRatingConstants.DEFAULT_SCORE);
        foodQualitySlider.setValue(currentUserRating != null ? currentUserRating.foodQuality() : RestaurantRatingConstants.DEFAULT_SCORE);
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
                employeesServiceSlider.getValue(),
                audioMusicSlider.getValue(),
                generalVibesSlider.getValue(),
                priceForQualitySlider.getValue(),
                locationLocaleSlider.getValue(),
                foodQualitySlider.getValue()
        );
        submitListener.onSubmit(rateRestaurantDisplay);
    }
}
