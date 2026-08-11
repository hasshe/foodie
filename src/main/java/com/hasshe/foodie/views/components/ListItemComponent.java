package com.hasshe.foodie.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.util.Assert;

public class ListItemComponent {

    private static final String CSS_CLASS = "list-item";

    private final HorizontalLayout layout = new HorizontalLayout();

    public ListItemComponent(String title, String subtitle, String trailingText, Runnable onClick) {
        Assert.hasText(title, "title must not be blank");

        layout.addClassName(CSS_CLASS);
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.getStyle()
                .set("padding", "var(--lumo-space-m)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("box-sizing", "border-box");

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-weight", "600");
        textLayout.add(titleSpan);

        if (subtitle != null && !subtitle.isBlank()) {
            Span subtitleSpan = new Span(subtitle);
            subtitleSpan.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            textLayout.add(subtitleSpan);
        }

        layout.add(textLayout);
        layout.setFlexGrow(1, textLayout);

        if (trailingText != null && !trailingText.isBlank()) {
            Span trailingSpan = new Span(trailingText);
            trailingSpan.getStyle().set("font-weight", "600").set("white-space", "nowrap");
            layout.add(trailingSpan);
        }

        if (onClick != null) {
            layout.getStyle().set("cursor", "pointer");
            layout.addClickListener(event -> onClick.run());
        }
    }

    public Component asComponent() {
        return layout;
    }
}
