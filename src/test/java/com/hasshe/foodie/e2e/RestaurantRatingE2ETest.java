package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class RestaurantRatingE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_restaurantSelected_when_viewingInfoDialog_then_showsFullDetailsBeforeRating() {
        registerAndLogin("infouser1");
        createGroup("Foodies");
        addRestaurantWithDetails("The Diner", "123 Main St", "American", "https://thediner.example");

        page.locator("vaadin-grid").getByText("The Diner").click();

        Locator dialogOverlay = page.locator("vaadin-dialog-overlay");
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("The Diner"))).isVisible();
        assertThat(dialogOverlay.getByText("123 Main St")).isVisible();
        assertThat(dialogOverlay.getByText("American")).isVisible();
        assertThat(dialogOverlay.getByText("https://thediner.example")).isVisible();
        assertThat(dialogOverlay.getByText("Foodies")).isVisible();
        assertThat(dialogOverlay.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Food items"))).isVisible();

        dialogOverlay.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Rate restaurant")).click();

        assertThat(page.getByText("Overall average")).isVisible();
    }

    @Test
    void given_unratedRestaurant_when_clickingRow_then_dialogShowsZeroAveragesAndAllCategories() {
        registerAndLogin("rateuser1");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");

        assertThat(page.getByText("Overall average")).isVisible();
        assertThat(page.getByText("Ratings submitted")).isVisible();
        assertThat(page.getByText("Food", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Service", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Vibe", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.locator("input[type=range]")).hasCount(3);
    }

    @Test
    void given_dialogOpen_when_savingDefaultRating_then_showsSuccessAndUpdatesAverageAndCount() {
        registerAndLogin("rateuser2");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();

        assertThat(page.getByText("Rating saved.")).isVisible();
        assertThat(page.getByText("50.0").first()).isVisible();
        assertThat(page.getByText("1", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void given_customSlidersSubmitted_when_reopeningDialog_then_prefillsSavedValues() {
        registerAndLogin("rateuser3");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        Locator foodSlider = page.locator("input[type=range]").nth(0);
        foodSlider.evaluate("el => { el.value = 90; el.dispatchEvent(new Event('input', { bubbles: true })); }");
        assertThat(page.getByText("90", new Page.GetByTextOptions().setExact(true))).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();
        assertThat(page.getByText("Rating saved.")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();

        openRestaurant("The Diner");

        assertThat(foodSlider).hasValue("90");
    }

    @Test
    void given_existingRating_when_savingUpdatedValues_then_ratingCountStaysOneAndAverageUpdates() {
        registerAndLogin("rateuser4");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();
        assertThat(page.getByText("Rating saved.")).isVisible();
        assertThat(page.getByText("1", new Page.GetByTextOptions().setExact(true))).isVisible();

        Locator foodSlider = page.locator("input[type=range]").nth(0);
        foodSlider.evaluate("el => { el.value = 100; el.dispatchEvent(new Event('input', { bubbles: true })); }");
        assertThat(page.getByText("100", new Page.GetByTextOptions().setExact(true))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();

        assertThat(page.getByText("Rating saved.")).isVisible();
        assertThat(page.getByText("1", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void given_ratingSavedAndDialogClosed_when_viewingGridBehindDialog_then_gridShowsUpdatedRatingImmediately() {
        registerAndLogin("rateuser5");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();
        assertThat(page.getByText("Rating saved.")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();

        assertThat(page.getByRole(AriaRole.GRIDCELL, new Page.GetByRoleOptions().setName("50.0"))).isVisible();
    }

    @Test
    void given_restaurantWithNoFoodItems_when_openingFoodItems_then_ratingSlidersAreShownAndAddingOneShowsItInListWithAverageRating() {
        registerAndLogin("fooditemuser1");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurantFoodItems("The Diner");

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Food items"))).isVisible();
        assertThat(page.locator("vaadin-dialog-overlay").getByText("Rating", new Locator.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.locator("input[type=range]:visible")).hasCount(1);

        page.getByLabel("Name").fill("Ribeye Steak");
        page.getByLabel("Dish category").fill("Steak");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add food item")).click();

        assertThat(page.getByText("Food item added.")).isVisible();
        assertThat(page.getByText("Ribeye Steak")).isVisible();
        assertThat(page.getByRole(AriaRole.GRIDCELL, new Page.GetByRoleOptions().setName("50.0"))).isVisible();
    }

    @Test
    void given_customRatingProvidedAtCreation_when_addingFoodItem_then_ratingIsSavedImmediatelyAndIndependentOfRestaurantRating() {
        registerAndLogin("fooditemuser2");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();
        assertThat(page.getByText("Rating saved.")).isVisible();
        assertThat(page.getByText("50.0").first()).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();

        openRestaurantFoodItems("The Diner");
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Food items"))).isVisible();
        assertThat(page.locator("input[type=range]:visible")).hasCount(1);

        Locator ratingSlider = page.locator("input[type=range]:visible").nth(0);
        ratingSlider.evaluate("el => { el.value = 95; el.dispatchEvent(new Event('input', { bubbles: true })); }");
        assertThat(page.getByText("95", new Page.GetByTextOptions().setExact(true))).isVisible();

        page.getByLabel("Name").fill("Ribeye Steak");
        page.getByLabel("Dish category").fill("Steak");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add food item")).click();
        assertThat(page.getByText("Food item added.")).isVisible();

        page.locator("vaadin-grid").getByText("Ribeye Steak").click();
        assertThat(page.getByText("Ratings submitted")).isVisible();
        assertThat(page.getByText("1", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.locator("input[type=range]:visible").nth(0)).hasValue("95");
    }

    private void registerAndLogin(String prefix) {
        String username = uniqueUsername(prefix);
        registerUser(username, "Rating User", "supersecret123");
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

    private void addRestaurant(String name, String address) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        page.getByLabel("Name").fill(name);
        page.getByLabel("Address").fill(address);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        assertThat(page.getByText("Restaurant added.")).isVisible();
    }

    private void addRestaurantWithDetails(String name, String address, String cuisineType, String website) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        page.getByLabel("Name").fill(name);
        page.getByLabel("Address").fill(address);
        page.getByLabel("Cuisine type").fill(cuisineType);
        page.getByLabel("Website").fill(website);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        assertThat(page.getByText("Restaurant added.")).isVisible();
    }

    private void openRestaurant(String name) {
        page.locator("vaadin-grid").getByText(name).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(name))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Rate restaurant")).click();
    }

    private void openRestaurantFoodItems(String name) {
        page.locator("vaadin-grid").getByText(name).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(name))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Food items")).click();
    }
}
