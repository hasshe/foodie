package com.hasshe.foodie.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.util.Assert;

public class DialogCloseButtonComponent {

    public DialogCloseButtonComponent(Dialog dialog) {
        Assert.notNull(dialog, "dialog must not be null");
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL), event -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        dialog.getHeader().add(closeButton);
    }
}
