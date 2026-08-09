package com.hasshe.foodie.config;

import com.hasshe.foodie.dto.RegisterUserDisplay;
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

    DevUserSeeder(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userService.findByUsername(DEMO_USERNAME).isEmpty()) {
            userService.registerUser(new RegisterUserDisplay(DEMO_USERNAME, DEMO_PASSWORD, DEMO_DISPLAY_NAME));
            log.info("Seeded demo user '{}' for local development", DEMO_USERNAME);
        }
    }
}
