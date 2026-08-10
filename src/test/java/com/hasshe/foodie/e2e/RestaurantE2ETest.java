package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class RestaurantE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_userWithNoGroups_when_clickingAddRestaurant_then_redirectsToGroupsWithError() {
        registerAndLogin("norestogroup");
        goToRestaurants();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();

        assertThat(page.getByText("You need to create a group before adding a restaurant.")).isVisible();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
    }

    @Test
    void given_oneGroup_when_addingRestaurant_then_appearsInListWithGroupPreSelected() {
        registerAndLogin("restuser");
        createGroup("Foodies");
        goToRestaurants();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        assertThat(page.locator("vaadin-dialog-overlay").getByLabel("Group")).containsText("Foodies");

        page.getByLabel("Name").fill("The Diner");
        page.getByLabel("Address").fill("123 Main St");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();

        assertThat(page.getByText("Restaurant added.")).isVisible();
        assertThat(page.getByText("The Diner")).isVisible();
    }

    @Test
    void given_missingRequiredFields_when_addingRestaurant_then_showsClientSideError() {
        registerAndLogin("restuser2");
        createGroup("Foodies");
        goToRestaurants();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();

        assertThat(page.getByText("Please fill in the required fields.")).isVisible();
    }

    @Test
    void given_twoGroups_when_openingAddRestaurantDialog_then_defaultGroupIsPreSelectedAndOthersSelectable() {
        registerAndLogin("restuser3");
        createGroup("Foodies");
        assertThat(page.getByText("Group created.")).isVisible();
        createGroup("Weekend Warriors");
        assertThat(page.getByText("Group created.")).isVisible();
        goToRestaurants();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        assertThat(page.locator("vaadin-dialog-overlay").getByLabel("Group")).containsText("Foodies");

        page.locator("vaadin-dialog-overlay").getByLabel("Group").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Weekend Warriors")).click();

        page.getByLabel("Name").fill("Pizza Place");
        page.getByLabel("Address").fill("456 Oak Ave");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();

        assertThat(page.getByText("Restaurant added.")).isVisible();
        assertThat(page.getByText("Pizza Place")).isVisible();
    }

    @Test
    void given_optionalFields_when_addingRestaurant_then_theyArePersistedAndVisibleAfterReload() {
        registerAndLogin("restuser4");
        createGroup("Foodies");
        goToRestaurants();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        page.getByLabel("Name").fill("The Diner");
        page.getByLabel("Address").fill("123 Main St");
        page.getByLabel("Cuisine type").fill("American");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        assertThat(page.getByText("Restaurant added.")).isVisible();

        page.reload();

        assertThat(page.getByText("The Diner")).isVisible();
    }

    private void registerAndLogin(String prefix) {
        String username = uniqueUsername(prefix);
        registerUser(username, "Restaurant User", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
        login(username, "supersecret123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    private void createGroup(String name) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Groups")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
        page.getByLabel("Group name").fill(name);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create group")).click();
    }

    private void goToRestaurants() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();
    }
}
