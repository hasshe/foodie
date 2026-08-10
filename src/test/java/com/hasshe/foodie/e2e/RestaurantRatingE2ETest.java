package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class RestaurantRatingE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_unratedRestaurant_when_clickingRow_then_dialogShowsZeroAveragesAndAllCategories() {
        registerAndLogin("rateuser1");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");

        assertThat(page.getByText("Overall average")).isVisible();
        assertThat(page.getByText("Ratings submitted")).isVisible();
        assertThat(page.getByText("Employees & Service", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Audio & Music", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("General Vibes", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Price for Quality", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Location & Locale", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Food Quality", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.locator("input[type=range]")).hasCount(6);
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
        Locator employeesServiceSlider = page.locator("input[type=range]").nth(0);
        employeesServiceSlider.evaluate("el => { el.value = 90; el.dispatchEvent(new Event('input', { bubbles: true })); }");
        assertThat(page.getByText("90", new Page.GetByTextOptions().setExact(true))).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();
        assertThat(page.getByText("Rating saved.")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();

        openRestaurant("The Diner");

        assertThat(employeesServiceSlider).hasValue("90");
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

        Locator employeesServiceSlider = page.locator("input[type=range]").nth(0);
        employeesServiceSlider.evaluate("el => { el.value = 100; el.dispatchEvent(new Event('input', { bubbles: true })); }");
        assertThat(page.getByText("100", new Page.GetByTextOptions().setExact(true))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save rating")).click();

        assertThat(page.getByText("Rating saved.")).isVisible();
        assertThat(page.getByText("1", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void given_restaurantWithNoFoodItems_when_openingFoodItems_then_ratingSlidersAreShownAndAddingOneShowsItInListWithAverageRating() {
        registerAndLogin("fooditemuser1");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");

        openRestaurant("The Diner");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Food items")).click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Food items"))).isVisible();
        assertThat(page.getByText("Taste", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Presentation", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Portion Quality", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Value for Price", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.locator("input[type=range]:visible")).hasCount(4);

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

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Food items")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Food items"))).isVisible();
        assertThat(page.locator("input[type=range]:visible")).hasCount(4);

        Locator tasteSlider = page.locator("input[type=range]:visible").nth(0);
        tasteSlider.evaluate("el => { el.value = 95; el.dispatchEvent(new Event('input', { bubbles: true })); }");
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

    private void openRestaurant(String name) {
        page.locator("vaadin-grid").getByText(name).click();
    }
}
