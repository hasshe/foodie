package com.hasshe.foodie.e2e;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class LoginE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_unauthenticatedUser_when_visitingHome_then_redirectedToLogin() {
        goTo("/");

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
    }

    @Test
    void given_validCredentials_when_login_then_showsHomePage() {
        login("demo", "password123");

        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    @Test
    void given_invalidPassword_when_login_then_showsIncorrectCredentialsError() {
        login("demo", "wrongpassword");

        assertThat(page.getByText("Incorrect username or password")).isVisible();
    }

    @Test
    void given_loggedInUser_when_logout_then_returnsToLoginAndBlocksHomeAgain() {
        login("demo", "password123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();

        logout();

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));

        goTo("/");
        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
    }
}
