package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileE2ETest extends AbstractFoodieE2ETest {

    private String username;

    @BeforeEach
    void registerLoginAndOpenProfile() {
        username = registerAndLogin("profileuser");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile"))).isVisible();
    }

    @Test
    void given_loggedInUser_when_updatingDisplayNameAndIcon_then_persistsAfterReload() {
        page.getByLabel("Display name").fill("Updated Name");
        selectIcon("Star");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();
        assertThat(page.getByText("Profile updated.")).isVisible();

        page.reload();

        assertEquals("Updated Name", page.getByLabel("Display name").inputValue());
        assertThat(page.getByLabel("Icon")).containsText("Star");
    }

    @Test
    void given_loggedInUser_when_selectingIconAndSaving_then_footerProfileIconIndicatorUpdatesImmediately() {
        assertEquals("vaadin:user", footerProfileIcon().getAttribute("icon"));

        selectIcon("Heart");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();
        assertThat(page.getByText("Profile updated.")).isVisible();

        assertEquals("vaadin:heart", footerProfileIcon().getAttribute("icon"));
    }

    @Test
    void given_loggedInUser_when_selectingIcon_then_footerProfileIconIndicatorSurvivesNavigation() {
        selectIcon("Heart");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();
        assertThat(page.getByText("Profile updated.")).isVisible();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();

        assertEquals("vaadin:heart", footerProfileIcon().getAttribute("icon"));
    }

    @Test
    void given_usernameTakenByAnotherUser_when_updatingProfile_then_showsError() {
        String otherUsername = uniqueUsername("otheruser");
        context.close();
        context = PlaywrightSupport.browser().newContext();
        page = context.newPage();
        registerUser(otherUsername, "Other User", DEFAULT_PASSWORD);
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();

        login(username, DEFAULT_PASSWORD);
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile")).click();
        page.getByLabel("Username").fill(otherUsername);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();

        assertThat(page.getByText("Username already taken: " + otherUsername)).isVisible();
    }

    @Test
    void given_wrongCurrentPassword_when_changingPassword_then_showsError() {
        page.getByLabel("Current password").fill("wrongpassword");
        page.getByLabel("New password", new Page.GetByLabelOptions().setExact(true)).fill("brandnewpassword1");
        page.getByLabel("Confirm new password").fill("brandnewpassword1");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Change password")).click();

        assertThat(page.getByText("Current password is incorrect")).isVisible();
    }

    @Test
    void given_mismatchedNewPasswords_when_changingPassword_then_showsClientSideError() {
        page.getByLabel("Current password").fill(DEFAULT_PASSWORD);
        page.getByLabel("New password", new Page.GetByLabelOptions().setExact(true)).fill("brandnewpassword1");
        page.getByLabel("Confirm new password").fill("somethingelse123");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Change password")).click();

        assertThat(page.getByText("New passwords do not match.")).isVisible();
    }

    @Test
    void given_correctCurrentPassword_when_changingPassword_then_forcesLogoutAndNewPasswordWorks() {
        page.getByLabel("Current password").fill(DEFAULT_PASSWORD);
        page.getByLabel("New password", new Page.GetByLabelOptions().setExact(true)).fill("brandnewpassword1");
        page.getByLabel("Confirm new password").fill("brandnewpassword1");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Change password")).click();

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));

        login(username, "brandnewpassword1");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    @Test
    void given_newUsername_when_updatingProfile_then_forcesLogoutAndNewUsernameWorks() {
        String newUsername = uniqueUsername("renamed");

        page.getByLabel("Username").fill(newUsername);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();

        assertThat(page).hasURL(Pattern.compile(".*/login.*"));

        login(newUsername, DEFAULT_PASSWORD);
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    private Locator footerProfileIcon() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Profile")).locator("vaadin-icon");
    }
}
