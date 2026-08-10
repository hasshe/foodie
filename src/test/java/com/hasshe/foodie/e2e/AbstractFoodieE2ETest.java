package com.hasshe.foodie.e2e;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.concurrent.atomic.AtomicLong;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("e2e")
abstract class AbstractFoodieE2ETest {

    protected static final String DEFAULT_PASSWORD = "supersecret123";

    private static final AtomicLong USERNAME_SEQUENCE = new AtomicLong(System.currentTimeMillis());

    @LocalServerPort
    protected int port;

    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void createPage() {
        context = PlaywrightSupport.browser().newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    protected void goTo(String path) {
        page.navigate("http://localhost:" + port + path);
    }

    protected String uniqueUsername(String prefix) {
        return prefix + USERNAME_SEQUENCE.incrementAndGet();
    }

    protected void login(String username, String password) {
        goTo("/login");
        page.getByLabel("Username").fill(username);
        page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();
    }

    protected void logout() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log out")).click();
    }

    protected void registerUser(String username, String displayName, String password) {
        goTo("/register");
        page.getByLabel("Username").fill(username);
        page.getByLabel("Display name").fill(displayName);
        page.getByLabel("Password", new Page.GetByLabelOptions().setExact(true)).fill(password);
        page.getByLabel("Confirm password").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register")).click();
    }

    protected String registerAndLogin(String usernamePrefix) {
        String username = uniqueUsername(usernamePrefix);
        registerUser(username, "Test User", DEFAULT_PASSWORD);
        assertThat(page.getByText("Registration successful. Please log in.")).isVisible();
        login(username, DEFAULT_PASSWORD);
        assertThat(page.getByText("Welcome to the first Vaadin page.")).isVisible();
        return username;
    }

    protected void selectIcon(String label) {
        page.getByLabel("Icon").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(label)).click();
    }

    protected void goToRestaurants() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Restaurants")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Restaurants"))).isVisible();
    }

    protected void goToWishlist() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Wishlist")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Wishlist"))).isVisible();
    }

    protected void createGroup(String name) {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Groups")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Groups").setExact(true))).isVisible();
        page.getByLabel("Group name").fill(name);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create group")).click();
        assertThat(page.getByText("Group created.")).isVisible();
    }

    protected void addRestaurant(String name, String address) {
        goToRestaurants();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add restaurant")).click();
        page.getByLabel("Name").fill(name);
        page.getByLabel("Address").fill(address);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add").setExact(true)).click();
        assertThat(page.getByText("Restaurant added.")).isVisible();
    }
}
