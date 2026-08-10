package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class GroupE2ETest extends AbstractFoodieE2ETest {

    @BeforeEach
    void registerLoginAndOpenGroups() {
        String username = uniqueUsername("groupuser");
        registerUser(username, "Group User", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
        login(username, "supersecret123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Groups")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
    }

    @Test
    void given_noGroups_when_openingGroupsView_then_showsEmptyState() {
        assertThat(page.getByText("You don't have any groups yet.")).isVisible();
    }

    @Test
    void given_validName_when_creatingGroup_then_appearsInListAndIsAutomaticallyDefault() {
        createGroup("Foodies");

        assertThat(page.getByText("Group created.")).isVisible();
        assertThat(page.getByText("Foodies")).isVisible();
        assertThat(page.getByText("Default")).isVisible();
    }

    @Test
    void given_blankName_when_creatingGroup_then_showsClientSideError() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create group")).click();

        assertThat(page.getByText("Please enter a group name.")).isVisible();
    }

    @Test
    void given_secondGroup_when_settingAsDefault_then_defaultBadgeMovesToIt() {
        createGroup("Foodies");
        assertThat(page.getByText("Group created.")).isVisible();
        createGroup("Weekend Warriors");
        assertThat(page.getByText("Group created.")).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Set as default")).click();

        assertThat(page.getByText("Default group updated.")).isVisible();
    }

    private void createGroup(String name) {
        page.getByLabel("Group name").fill(name);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create group")).click();
    }
}
