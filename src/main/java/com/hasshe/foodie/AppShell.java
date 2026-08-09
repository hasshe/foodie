package com.hasshe.foodie;

import com.hasshe.foodie.constants.ThemeConstants;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Theme(value = ThemeConstants.THEME_NAME, variant = Lumo.DARK)
@PWA(name = "Foodie", shortName = "Foodie")
@Push
public class AppShell implements AppShellConfigurator {
}
