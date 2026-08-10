package com.hasshe.foodie.e2e;

import com.hasshe.foodie.constants.SecurityConstants;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RememberMeE2ETest extends AbstractFoodieE2ETest {

    private static final double THIRTY_DAYS_IN_SECONDS = 30 * 24 * 60 * 60;

    @Test
    void given_successfulLogin_then_setsRememberMeCookieWithThirtyDayExpiry() {
        login("demo", "password123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();

        Optional<Cookie> rememberMeCookie = findRememberMeCookie();
        assertTrue(rememberMeCookie.isPresent(), "remember-me cookie should be set after login");

        double expiresInSeconds = rememberMeCookie.get().expires - (System.currentTimeMillis() / 1000.0);
        assertTrue(
                Math.abs(expiresInSeconds - THIRTY_DAYS_IN_SECONDS) < 60,
                "remember-me cookie should expire in ~30 days, was " + expiresInSeconds + " seconds"
        );
    }

    @Test
    void given_rememberMeCookie_when_reopeningBrowser_then_staysLoggedIn() throws Exception {
        login("demo", "password123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();

        Path storageStatePath = Files.createTempFile("foodie-storage-state", ".json");
        context.storageState(new BrowserContext.StorageStateOptions().setPath(storageStatePath));
        context.close();

        context = PlaywrightSupport.browser().newContext(new Browser.NewContextOptions().setStorageStatePath(storageStatePath));
        page = context.newPage();

        goTo("/");

        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    @Test
    void given_rememberMeCookie_when_loggingOut_then_cookieIsCleared() {
        login("demo", "password123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
        assertTrue(findRememberMeCookie().isPresent());

        logout();
        assertThat(page).hasURL(Pattern.compile(".*/login.*"));

        assertFalse(findRememberMeCookie().isPresent(), "remember-me cookie should be cleared on logout");
    }

    @Test
    void given_rememberMeCookie_when_changingUsername_then_cookieIsCleared() {
        registerAndLogin("rmuser");
        assertTrue(findRememberMeCookie().isPresent());

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile"))).isVisible();

        page.getByLabel("Username").fill(uniqueUsername("renamed"));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
        assertFalse(findRememberMeCookie().isPresent(), "remember-me cookie should be cleared after username change");
    }

    @Test
    void given_rememberMeCookie_when_changingPassword_then_cookieIsCleared() {
        registerAndLogin("rmuser");
        assertTrue(findRememberMeCookie().isPresent());

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile"))).isVisible();

        page.getByLabel("Current password").fill(DEFAULT_PASSWORD);
        page.getByLabel("New password", new Page.GetByLabelOptions().setExact(true)).fill("brandnewpassword1");
        page.getByLabel("Confirm new password").fill("brandnewpassword1");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Change password")).click();

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
        assertFalse(findRememberMeCookie().isPresent(), "remember-me cookie should be cleared after password change");
    }

    private Optional<Cookie> findRememberMeCookie() {
        return context.cookies().stream()
                .filter(cookie -> SecurityConstants.REMEMBER_ME_COOKIE_NAME.equals(cookie.name))
                .findFirst();
    }
}
