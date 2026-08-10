package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class RegistrationE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_newUsername_when_register_then_redirectsToLoginWithSuccessMessage() {
        String username = uniqueUsername("newchef");

        registerUser(username, "New Chef", "supersecret123");

        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
    }

    @Test
    void given_registeredUser_when_login_then_succeeds() {
        String username = uniqueUsername("roundtrip");
        registerUser(username, "Round Trip", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();

        login(username, "supersecret123");

        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    @Test
    void given_duplicateUsername_when_register_then_showsUsernameTakenError() {
        String username = uniqueUsername("dupe");
        registerUser(username, "First One", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();

        registerUser(username, "Second One", "anothersecret1");

        assertThat(page.getByText("Username already taken: " + username)).isVisible();
    }

    @Test
    void given_mismatchedPasswords_when_register_then_showsClientSideError() {
        String username = uniqueUsername("mismatch");
        goTo("/register");
        page.getByLabel("Username").fill(username);
        page.getByLabel("Display name").fill("Mismatch User");
        page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill("supersecret123");
        page.getByLabel("Confirm password").fill("differentpassword1");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register")).click();

        assertThat(page.getByText("Passwords do not match.")).isVisible();
    }

    @Test
    void given_shortPassword_when_register_then_showsClientSideError() {
        String username = uniqueUsername("shortpw");
        goTo("/register");
        page.getByLabel("Username").fill(username);
        page.getByLabel("Display name").fill("Short Password User");
        page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill("abc");
        page.getByLabel("Confirm password").fill("abc");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register")).click();

        assertThat(page.getByText("Password must be at least 8 characters.")).isVisible();
    }
}
