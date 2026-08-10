package com.hasshe.foodie.config;

import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.AddRestaurantDisplay;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.service.api.GroupService;
import com.hasshe.foodie.service.api.RestaurantService;
import com.hasshe.foodie.service.api.UserService;
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

    DevUserSeeder(UserService userService, GroupService groupService, RestaurantService restaurantService) {
        this.userService = userService;
        this.groupService = groupService;
        this.restaurantService = restaurantService;
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

        restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "The Diner", "123 Main St", "American", "https://thediner.example", null, foodies.id()));
        restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "Pizza Place", "456 Oak Ave", "Italian", null, null, foodies.id()));
        restaurantService.addRestaurant(DEMO_USERNAME, new AddRestaurantDisplay(
                "Sushi Spot", "789 Pine Rd", "Japanese", null, null, weekendWarriors.id()));

        log.info("Seeded 2 sample groups and 3 sample restaurants for username {}", DEMO_USERNAME);
    }
}
