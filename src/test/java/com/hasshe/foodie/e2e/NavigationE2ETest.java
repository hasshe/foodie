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
        assertThat(footerLink("Home")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingGroups_then_navigatesThereAndFooterPersists() {
        footerLink("Groups").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
        assertThat(page.getByLabel("Group name")).isVisible();
        assertThat(footerLink("Home")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingWishlist_then_navigatesThereAndFooterPersists() {
        footerLink("Wishlist").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to wishlist"))).isVisible();
        assertThat(footerLink("Home")).isVisible();
    }

    @Test
    void given_loggedInUser_when_clickingProfile_then_navigatesToProfilePage() {
        footerLink("Profile").click();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Profile"))).isVisible();
        assertThat(page.getByLabel("Username")).isVisible();
    }

    @Test
    void given_loggedInUser_when_navigatingAwayFromHomeAndBack_then_homeContentReturns() {
        footerLink("Wishlist").click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();

        footerLink("Home").click();

        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
    }

    private Locator footerLink(String name) {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(name));
    }
}
