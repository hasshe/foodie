package com.hasshe.foodie.views.components;

import com.hasshe.foodie.constants.RestaurantRatingConstants;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import org.springframework.util.Assert;

public class RatingSliderComponent extends VerticalLayout {

    private final Element sliderElement = new Element("input");
    private final Span valueLabel = new Span();

    public RatingSliderComponent(String categoryLabel, int initialValue) {
        Assert.hasText(categoryLabel, "categoryLabel must not be blank");

        sliderElement.setAttribute("type", "range");
        sliderElement.setAttribute("min", String.valueOf(RestaurantRatingConstants.MIN_SCORE));
        sliderElement.setAttribute("max", String.valueOf(RestaurantRatingConstants.MAX_SCORE));
        sliderElement.getStyle().set("width", "100%");
        sliderElement.addPropertyChangeListener("value", "input", event -> updateValueLabel());

        Div sliderWrapper = new Div();
        sliderWrapper.setWidthFull();
        sliderWrapper.getElement().appendChild(sliderElement);

        HorizontalLayout header = new HorizontalLayout(new Span(categoryLabel), valueLabel);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        setPadding(false);
        setSpacing(false);
        setWidthFull();
        add(header, sliderWrapper);

        setValue(initialValue);
    }

    public int getValue() {
        return sliderElement.getProperty("value", RestaurantRatingConstants.DEFAULT_SCORE);
    }

    public void setValue(int value) {
        Assert.isTrue(
                value >= RestaurantRatingConstants.MIN_SCORE && value <= RestaurantRatingConstants.MAX_SCORE,
                "value must be between " + RestaurantRatingConstants.MIN_SCORE + " and " + RestaurantRatingConstants.MAX_SCORE
        );
        sliderElement.setProperty("value", (double) value);
        updateValueLabel();
    }

    private void updateValueLabel() {
        valueLabel.setText(String.valueOf(getValue()));
    }
}
