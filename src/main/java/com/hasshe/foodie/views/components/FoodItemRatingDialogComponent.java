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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.util.Assert;

public class FoodItemRatingDialogComponent {

    public interface SubmitListener {
        void onSubmit(RateFoodItemDisplay rateFoodItemDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final RatingSummaryHeaderComponent ratingSummaryHeaderComponent = new RatingSummaryHeaderComponent();
    private final RatingSliderComponent ratingSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_RATING, FoodItemRatingConstants.DEFAULT_SCORE);

    private SubmitListener submitListener = rateFoodItemDisplay -> {};

    public FoodItemRatingDialogComponent() {
        dialog.setWidth("320px");
        new DialogCloseButtonComponent(dialog);

        Button saveButton = new Button("Save rating", event -> handleSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttons = new HorizontalLayout(saveButton);
        buttons.setWidthFull();
        buttons.getStyle().set("flex-wrap", "wrap");

        VerticalLayout content = new VerticalLayout(
                ratingSummaryHeaderComponent.asComponent(),
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

        ratingSummaryHeaderComponent.refresh(foodItemRatingSummaryDisplay.averageRating(), foodItemRatingSummaryDisplay.ratingCount());

        FoodItemRatingDisplay currentUserRating = foodItemRatingSummaryDisplay.currentUserRating();
        ratingSlider.setValue(currentUserRating != null ? currentUserRating.rating() : FoodItemRatingConstants.DEFAULT_SCORE);
    }

    private void handleSave() {
        RateFoodItemDisplay rateFoodItemDisplay = new RateFoodItemDisplay(ratingSlider.getValue());
        submitListener.onSubmit(rateFoodItemDisplay);
    }
}
