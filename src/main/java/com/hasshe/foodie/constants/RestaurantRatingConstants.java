package com.hasshe.foodie.constants;

public final class RestaurantRatingConstants {

    private RestaurantRatingConstants() {}

    public static final String TABLE_RESTAURANT_RATINGS = "restaurant_ratings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FOOD = "food";
    public static final String COLUMN_SERVICE = "service";
    public static final String COLUMN_VIBE = "vibe";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final int MIN_SCORE = 1;
    public static final int MAX_SCORE = 100;
    public static final int DEFAULT_SCORE = 50;

    public static final String CATEGORY_FOOD = "Food";
    public static final String CATEGORY_SERVICE = "Service";
    public static final String CATEGORY_VIBE = "Vibe";
}
