package com.hasshe.foodie.constants;

public final class FoodItemConstants {

    private FoodItemConstants() {}

    public static final String TABLE_FOOD_ITEMS = "food_items";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DISH_CATEGORY = "dish_category";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final int NAME_MAX_LENGTH = 150;
    public static final int DISH_CATEGORY_MAX_LENGTH = 100;
}
