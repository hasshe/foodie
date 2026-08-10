package com.hasshe.foodie.constants;

public final class FoodItemRatingConstants {

    private FoodItemRatingConstants() {}

    public static final String TABLE_FOOD_ITEM_RATINGS = "food_item_ratings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FOOD_ITEM_ID = "food_item_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final int MIN_SCORE = 1;
    public static final int MAX_SCORE = 100;
    public static final int DEFAULT_SCORE = 50;

    public static final String CATEGORY_RATING = "Rating";
}
