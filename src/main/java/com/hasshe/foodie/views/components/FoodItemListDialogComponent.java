package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.FoodItemRatingConstants;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.dto.FoodItemDisplay;
import com.hasshe.foodie.dto.RateFoodItemDisplay;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Locale;

public class FoodItemListDialogComponent {

    public interface AddFoodItemListener {
        void onAdd(AddFoodItemDisplay addFoodItemDisplay, RateFoodItemDisplay rateFoodItemDisplay);
    }

    public interface FoodItemSelectedListener {
        void onSelect(FoodItemDisplay foodItemDisplay);
    }

    private final Dialog dialog = new Dialog();
    private final Grid<FoodItemDisplay> foodItemGrid = new Grid<>(FoodItemDisplay.class, false);
    private final TextField nameField = new TextField("Name");
    private final TextField dishCategoryField = new TextField("Dish category");
    private final RatingSliderComponent ratingSlider =
            new RatingSliderComponent(FoodItemRatingConstants.CATEGORY_RATING, FoodItemRatingConstants.DEFAULT_SCORE);

    private AddFoodItemListener addFoodItemListener = (addFoodItemDisplay, rateFoodItemDisplay) -> {};
    private FoodItemSelectedListener foodItemSelectedListener = foodItemDisplay -> {};

    public FoodItemListDialogComponent() {
        dialog.setHeaderTitle("Food items");
        dialog.setWidth("420px");
        new DialogCloseButtonComponent(dialog);

        foodItemGrid.addColumn(FoodItemDisplay::name).setHeader("Name");
        foodItemGrid.addColumn(FoodItemDisplay::dishCategory).setHeader("Category");
        foodItemGrid.addColumn(this::formatAverageRating).setHeader("Avg rating");
        foodItemGrid.setWidthFull();
        foodItemGrid.setHeight("200px");
        foodItemGrid.addItemClickListener(event -> foodItemSelectedListener.onSelect(event.getItem()));

        nameField.setWidthFull();
        dishCategoryField.setWidthFull();

        Button addButton = new Button("Add food item", event -> handleAdd());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout formLayout = new VerticalLayout(
                nameField,
                dishCategoryField,
                new Span("Your rating"),
                ratingSlider,
                addButton
        );
        formLayout.setPadding(false);

        VerticalLayout content = new VerticalLayout(foodItemGrid, new Hr(), formLayout);
        content.setPadding(false);
        dialog.add(content);
    }

    public void open(List<FoodItemDisplay> foodItems, AddFoodItemListener addFoodItemListener, FoodItemSelectedListener foodItemSelectedListener) {
        Assert.notNull(foodItems, "foodItems must not be null");
        Assert.notNull(addFoodItemListener, "addFoodItemListener must not be null");
        Assert.notNull(foodItemSelectedListener, "foodItemSelectedListener must not be null");
        this.addFoodItemListener = addFoodItemListener;
        this.foodItemSelectedListener = foodItemSelectedListener;
        resetForm();
        refresh(foodItems);
        dialog.open();
    }

    public void refresh(List<FoodItemDisplay> foodItems) {
        Assert.notNull(foodItems, "foodItems must not be null");
        foodItemGrid.setItems(foodItems);
    }

    public void close() {
        dialog.close();
    }

    private void handleAdd() {
        if (nameField.isEmpty() || dishCategoryField.isEmpty()) {
            Notification.show("Please fill in the required fields.");
            return;
        }
        AddFoodItemDisplay addFoodItemDisplay = new AddFoodItemDisplay(nameField.getValue(), dishCategoryField.getValue());
        RateFoodItemDisplay rateFoodItemDisplay = new RateFoodItemDisplay(ratingSlider.getValue());
        addFoodItemListener.onAdd(addFoodItemDisplay, rateFoodItemDisplay);
        resetForm();
    }

    private String formatAverageRating(FoodItemDisplay foodItemDisplay) {
        if (foodItemDisplay.ratingCount() == 0) {
            return "No ratings";
        }
        return String.format(Locale.US, "%.1f", foodItemDisplay.averageRating());
    }

    private void resetForm() {
        nameField.clear();
        dishCategoryField.clear();
        ratingSlider.setValue(FoodItemRatingConstants.DEFAULT_SCORE);
    }
}
