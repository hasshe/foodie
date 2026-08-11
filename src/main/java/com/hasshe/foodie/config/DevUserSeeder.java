package com.hasshe.foodie.config;

import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.AddFoodItemDisplay;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.service.api.FoodItemService;
import com.hasshe.foodie.service.api.GroupService;
import com.hasshe.foodie.service.api.RestaurantService;
import com.hasshe.foodie.service.api.UserService;
import com.hasshe.foodie.service.api.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
class DevUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevUserSeeder.class);

    private static final String DEMO_USERNAME = "demo";
    private static final String DEMO_PASSWORD = "password123";
    private static final String DEMO_DISPLAY_NAME = "Demo User";

    private final UserService userService;
    private final GroupService groupService;
    private final RestaurantService restaurantService;
    private final FoodItemService foodItemService;
    private final WishlistService wishlistService;

    DevUserSeeder(
            UserService userService,
            GroupService groupService,
            RestaurantService restaurantService,
            FoodItemService foodItemService,
            WishlistService wishlistService
    ) {
        this.userService = userService;
        this.groupService = groupService;
        this.restaurantService = restaurantService;
        this.foodItemService = foodItemService;
        this.wishlistService = wishlistService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userService.findByUsername(DEMO_USERNAME).isEmpty()) {
            userService.registerUser(new RegisterUserDisplay(DEMO_USERNAME, DEMO_PASSWORD, DEMO_DISPLAY_NAME));
            log.info("Seeded demo user '{}' for local development", DEMO_USERNAME);
        }

        if (groupService.listGroupsForUser(DEMO_USERNAME).isEmpty()) {
            seedSampleGroupsAndRestaurants();
        }
    }

    private void seedSampleGroupsAndRestaurants() {
        GroupDomain foodies = groupService.createGroup(DEMO_USERNAME, new CreateGroupDisplay("Foodies"));
        GroupDomain weekendWarriors = groupService.createGroup(DEMO_USERNAME, new CreateGroupDisplay("Weekend Warriors"));

        RestaurantDomain theDiner = restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "The Diner", "123 Main St", "American", "https://thediner.example", null, foodies.id()));
        RestaurantDomain pizzaPlace = restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "Pizza Place", "456 Oak Ave", "Italian", null, null, foodies.id()));
        RestaurantDomain sushiSpot = restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "Sushi Spot", "789 Pine Rd", "Japanese", null, null, weekendWarriors.id()));

        log.info("Seeded 2 sample groups and 3 sample restaurants for username {}", DEMO_USERNAME);

        seedSampleFoodItems(theDiner.id(), pizzaPlace.id(), sushiSpot.id());
        seedSampleWishlistItems(foodies.id(), weekendWarriors.id());
    }

    private void seedSampleFoodItems(Long theDinerId, Long pizzaPlaceId, Long sushiSpotId) {
        foodItemService.addFoodItem(DEMO_USERNAME, theDinerId, new AddFoodItemDisplay("Ribeye Steak", "Steak"));
        foodItemService.addFoodItem(DEMO_USERNAME, theDinerId, new AddFoodItemDisplay("Caesar Salad", "Salad"));
        foodItemService.addFoodItem(DEMO_USERNAME, theDinerId, new AddFoodItemDisplay("Mozzarella Sticks", "Appetizer"));

        foodItemService.addFoodItem(DEMO_USERNAME, pizzaPlaceId, new AddFoodItemDisplay("Margherita Pizza", "Pizza"));
        foodItemService.addFoodItem(DEMO_USERNAME, pizzaPlaceId, new AddFoodItemDisplay("Caesar Salad", "Salad"));
        foodItemService.addFoodItem(DEMO_USERNAME, pizzaPlaceId, new AddFoodItemDisplay("Garlic Knots", "Appetizer"));

        foodItemService.addFoodItem(DEMO_USERNAME, sushiSpotId, new AddFoodItemDisplay("California Roll", "Sushi"));
        foodItemService.addFoodItem(DEMO_USERNAME, sushiSpotId, new AddFoodItemDisplay("Miso Soup", "Soup"));
        foodItemService.addFoodItem(DEMO_USERNAME, sushiSpotId, new AddFoodItemDisplay("Edamame", "Appetizer"));

        log.info("Seeded 9 sample food items across 3 restaurants for username {}", DEMO_USERNAME);
    }

    private void seedSampleWishlistItems(Long foodiesGroupId, Long weekendWarriorsGroupId) {
        wishlistService.addToWishlist(DEMO_USERNAME, new AddRestaurantDisplay(
                "Taco Truck", "321 Elm St", "Mexican", null, null, weekendWarriorsGroupId));
        wishlistService.addToWishlist(DEMO_USERNAME, new AddRestaurantDisplay(
                "Ramen House", "654 Cedar Blvd", "Japanese", null, null, weekendWarriorsGroupId));
        wishlistService.addToWishlist(DEMO_USERNAME, new AddRestaurantDisplay(
                "Burger Barn", "987 Birch Ln", "American", "https://burgerbarn.example", null, foodiesGroupId));

        log.info("Seeded 3 sample wishlist items for username {}", DEMO_USERNAME);
    }
}
