package com.hasshe.foodie.config;

import com.hasshe.foodie.db.api.UserIconDb;
import com.hasshe.foodie.db.entity.UserIconEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class IconCatalogSeeder implements ApplicationRunner {

    private record IconSeed(String iconKey, String label) {}

    private static final Logger log = LoggerFactory.getLogger(IconCatalogSeeder.class);

    private static final List<IconSeed> DEFAULT_ICONS = List.of(
            new IconSeed("USER", "Classic"),
            new IconSeed("CUTLERY", "Fork & Knife"),
            new IconSeed("STAR", "Star"),
            new IconSeed("HEART", "Heart"),
            new IconSeed("COFFEE", "Coffee"),
            new IconSeed("FIRE", "Fire"),
            new IconSeed("MOON", "Moon"),
            new IconSeed("THUMBS_UP", "Thumbs Up")
    );

    private final UserIconDb userIconDb;

    IconCatalogSeeder(UserIconDb userIconDb) {
        this.userIconDb = userIconDb;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userIconDb.findAll().isEmpty()) {
            DEFAULT_ICONS.forEach(icon -> userIconDb.save(new UserIconEntity(icon.iconKey(), icon.label())));
            log.info("Seeded {} default user icons", DEFAULT_ICONS.size());
        }
    }
}
