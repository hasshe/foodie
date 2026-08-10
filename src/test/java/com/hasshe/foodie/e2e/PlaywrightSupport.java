package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

final class PlaywrightSupport {

    private static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));

    private static final Playwright PLAYWRIGHT = Playwright.create();
    private static final Browser BROWSER = PLAYWRIGHT.chromium()
            .launch(new BrowserType.LaunchOptions()
                    .setHeadless(HEADLESS)
                    .setSlowMo(HEADLESS ? 0 : 250));

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            BROWSER.close();
            PLAYWRIGHT.close();
        }));
    }

    private PlaywrightSupport() {}

    static Browser browser() {
        return BROWSER;
    }
}
