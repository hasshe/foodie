package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class FoodListE2ETest extends AbstractFoodieE2ETest {

    @Test
    void given_onlyOneFoodItemInCategory_when_viewingFoodList_then_categoryIsNotShown() {
        registerAndLogin("foodlistuser1");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");
        addFoodItem("The Diner", "Ribeye Steak", "Steak");

        goToFoodList();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Steak"))).not().isVisible();
        assertThat(page.getByText("No food items to compare yet.", new Page.GetByTextOptions().setExact(false))).isVisible();
    }

    @Test
    void given_twoFoodItemsSameCategoryAtDifferentRestaurants_when_viewingFoodList_then_categoryIsShownWithBothItems() {
        registerAndLogin("foodlistuser2");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");
        addFoodItem("The Diner", "Ribeye Steak", "Steak");
        addRestaurant("Steakhouse", "456 Oak Ave");
        addFoodItem("Steakhouse", "Filet Mignon", "Steak");

        goToFoodList();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Steak"))).isVisible();
        assertThat(page.getByText("Ribeye Steak")).isVisible();
        assertThat(page.getByText("Filet Mignon")).isVisible();
        assertThat(page.getByText("The Diner", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("Steakhouse", new Page.GetByTextOptions().setExact(true))).isVisible();
    }

    @Test
    void given_twoFoodItemsInDifferentCategories_when_viewingFoodList_then_neitherCategoryIsShown() {
        registerAndLogin("foodlistuser3");
        createGroup("Foodies");
        addRestaurant("The Diner", "123 Main St");
        addFoodItem("The Diner", "Ribeye Steak", "Steak");
        addFoodItem("The Diner", "Caesar Salad", "Salad");

        goToFoodList();

        assertThat(page.getByText("No food items to compare yet.", new Page.GetByTextOptions().setExact(false))).isVisible();
    }

    private void addFoodItem(String restaurantName, String foodItemName, String dishCategory) {
        goToRestaurants();
        page.locator("vaadin-grid").getByText(restaurantName).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(restaurantName))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Food items")).click();

        page.getByLabel("Name").fill(foodItemName);
        page.getByLabel("Dish category").fill(dishCategory);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add food item")).click();
        assertThat(page.getByText("Food item added.")).isVisible();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();
    }

    private void goToFoodList() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("FoodList")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("FoodList"))).isVisible();
    }
}
