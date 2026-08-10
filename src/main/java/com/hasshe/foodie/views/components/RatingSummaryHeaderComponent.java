package com.hasshe.foodie.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class RatingSummaryHeaderComponent {

    private final Span overallAverageValue = new Span();
    private final Span ratingCountValue = new Span();
    private final VerticalLayout layout = new VerticalLayout();
    private final RatingFormatter ratingFormatter = new RatingFormatter();

    public RatingSummaryHeaderComponent() {
        HorizontalLayout overallRow = new HorizontalLayout(new Span("Overall average"), overallAverageValue);
        overallRow.setWidthFull();
        overallRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout countRow = new HorizontalLayout(new Span("Ratings submitted"), ratingCountValue);
        countRow.setWidthFull();
        countRow.setJustifyContentMode(JustifyContentMode.BETWEEN);

        layout.add(overallRow, countRow);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();
    }

    public Component asComponent() {
        return layout;
    }

    public void refresh(double overallAverage, int ratingCount) {
        overallAverageValue.setText(ratingFormatter.format(overallAverage));
        ratingCountValue.setText(String.valueOf(ratingCount));
    }
}
