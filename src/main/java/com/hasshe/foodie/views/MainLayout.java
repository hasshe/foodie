package com.hasshe.foodie.views;

import com.hasshe.foodie.views.components.FooterMenuComponent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div content = new Div();

    public MainLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        content.setSizeFull();
        setFlexGrow(1, content);

        add(content, new FooterMenuComponent());
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.content.getElement().appendChild(content.getElement());
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        content.removeAll();
    }
}
