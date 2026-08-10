package com.hasshe.foodie.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class NavigationE2ETest extends AbstractFoodieE2ETest {

    @BeforeEach
    void registerAndLogin() {
        String username = uniqueUsername("navuser");
        registerUser(username, "Nav User", "supersecret123");
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
        login(username, "supersecret123");
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingRestaurants_then_navigatesThereAndFooterPersists() {
        footerLink("Restaurants").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant"))).isVisible();
        assertThat(footerLink("Profile")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingGroups_then_navigatesThereAndFooterPersists() {
        footerLink("Groups").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
        assertThat(page.getByLabel("Group name")).isVisible();
        assertThat(footerLink("Profile")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingWishlist_then_navigatesThereAndFooterPersists() {
        footerLink("Wishlist").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist"))).isVisible();
        assertThat(footerLink("Profile")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingProfile_then_navigatesToProfilePage() {
        footerLink("Profile").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile"))).isVisible();
        assertThat(page.getByLabel("Username")).isVisible();
    }

    @Test
    void given_loggedInUserWithNoComparableFoodItems_when_clickingFoodList_then_navigatesThereAndShowsEmptyState() {
        footerLink("FoodList").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("FoodList"))).isVisible();
        assertThat(page.getByText("No food items to compare yet.", new Page.GetByTextOptions().setExact(false))).isVisible();
        assertThat(footerLink("Profile")).isVisible();
    }

    @Test
    void given_loggedInUser_when_footerMenuIsShown_then_homeLinkIsNotPresent() {
        assertThat(footerLink("Home")).not().isVisible();
    }

    @Test
    void given_loggedInUser_when_navigatingAwayAndBackToRootUrl_then_welcomePageReturns() {
        footerLink("Wishlist").click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();

        goTo("/");

        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    private Locator footerLink(String name) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(name));
    }
}
