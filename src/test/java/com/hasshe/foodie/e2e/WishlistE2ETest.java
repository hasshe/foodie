package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class WishlistE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_userWithNoGroups_when_addingToWishlist_then_redirectsToGroupsWithError() {
        registerAndLogin("wishuser1");
        goToWishlist();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist")).click();

        assertThat(page.getByText("You need to create a group before adding to the wishlist.")).isVisible();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
    }

    @Test
    void given_oneGroup_when_addingToWishlist_then_appearsInWishlistWithGroupPreSelectedAndNotInRestaurants() {
        registerAndLogin("wishuser2");
        createGroup("Foodies");
        goToWishlist();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist")).click();
        assertThat(page.getByLabel("Group")).containsText("Foodies");

        page.getByLabel("Name").fill("The Diner");
        page.getByLabel("Address").fill("123 Main St");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();

        assertThat(page.getByText("Added to wishlist.")).isVisible();
        assertThat(page.getByText("The Diner")).isVisible();

        goToRestaurants();
        assertThat(page.locator("vaadin-grid").getByText("The Diner")).not().isVisible();
    }

    @Test
    void given_missingRequiredFields_when_addingToWishlist_then_showsClientSideError() {
        registerAndLogin("wishuser3");
        createGroup("Foodies");
        goToWishlist();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();

        assertThat(page.getByText("Please fill in the required fields.")).isVisible();
    }

    @Test
    void given_wishlistItem_when_checkingOffAndRatingLater_then_movesToRestaurantsMenuUnrated() {
        registerAndLogin("wishuser4");
        createGroup("Foodies");
        goToWishlist();
        addToWishlist("The Diner", "123 Main St");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Check off")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Mark \"The Diner\" as visited?"))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rate later")).click();

        assertThat(page.getByText("Marked as visited.")).isVisible();
        assertThat(page.locator("vaadin-grid").getByText("The Diner")).not().isVisible();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();

        goToRestaurants();
        assertThat(page.locator("vaadin-grid").getByText("The Diner")).isVisible();
    }

    @Test
    void given_wishlistItem_when_checkingOffAndRatingNow_then_navigatesToRestaurantsAndOpensRatingDialog() {
        registerAndLogin("wishuser5");
        createGroup("Foodies");
        goToWishlist();
        addToWishlist("The Diner", "123 Main St");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Check off")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Mark \"The Diner\" as visited?"))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rate now")).click();
        assertThat(page.getByText("Marked as visited.")).isVisible();
        page.waitForURL(url -> url.contains("restaurants"));

        assertThat(page.getByText("The Diner", new Page.GetByTextOptions().setExact(true)).first()).isVisible();
        assertThat(page.getByText("Overall average")).isVisible();
        assertThat(page.getByText("Employees & Service", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void given_checkOffPrompt_when_cancelled_then_itemStaysOnWishlist() {
        registerAndLogin("wishuser6");
        createGroup("Foodies");
        goToWishlist();
        addToWishlist("The Diner", "123 Main St");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Check off")).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).click();

        assertThat(page.locator("vaadin-grid").getByText("The Diner")).isVisible();
    }

    private void registerAndLogin(String prefix) {
        String username = uniqueUsername(prefix);
        registerUser(username, "Wishlist User", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
        login(username, "supersecret123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    private void createGroup(String name) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Groups")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
        page.getByLabel("Group name").fill(name);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create group")).click();
        assertThat(page.getByText("Group created.")).isVisible();
    }

    private void goToWishlist() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Wishlist")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();
    }

    private void goToRestaurants() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();
    }

    private void addToWishlist(String name, String address) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist")).click();
        page.getByLabel("Name").fill(name);
        page.getByLabel("Address").fill(address);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        assertThat(page.getByText("Added to wishlist.")).isVisible();
    }
}
