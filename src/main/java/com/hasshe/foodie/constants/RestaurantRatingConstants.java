package com.hasshe.foodie.constants;

public final class RestaurantRatingConstants {

    private RestaurantRatingConstants() {}

    public static final String TABLE_RESTAURANT_RATINGS = "restaurant_ratings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMPLOYEES_SERVICE = "employees_service";
    public static final String COLUMN_AUDIO_MUSIC = "audio_music";
    public static final String COLUMN_GENERAL_VIBES = "general_vibes";
    public static final String COLUMN_PRICE_FOR_QUALITY = "price_for_quality";
    public static final String COLUMN_LOCATION_LOCALE = "location_locale";
    public static final String COLUMN_FOOD_QUALITY = "food_quality";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final int MIN_SCORE = 1;
    public static final int MAX_SCORE = 100;
    public static final int DEFAULT_SCORE = 50;

    public static final String CATEGORY_EMPLOYEES_SERVICE = "Employees & Service";
    public static final String CATEGORY_AUDIO_MUSIC = "Audio & Music";
    public static final String CATEGORY_GENERAL_VIBES = "General Vibes";
    public static final String CATEGORY_PRICE_FOR_QUALITY = "Price for Quality";
    public static final String CATEGORY_LOCATION_LOCALE = "Location & Locale";
    public static final String CATEGORY_FOOD_QUALITY = "Food Quality";
}
