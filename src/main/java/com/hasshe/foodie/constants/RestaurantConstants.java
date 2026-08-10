package com.hasshe.foodie.constants;

public final class RestaurantConstants {

    private RestaurantConstants() {}

    public static final String TABLE_RESTAURANTS = "restaurants";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_CUISINE_TYPE = "cuisine_type";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_WISHLIST = "wishlist";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    public static final int NAME_MAX_LENGTH = 150;
    public static final int ADDRESS_MAX_LENGTH = 255;
    public static final int CUISINE_TYPE_MAX_LENGTH = 100;
    public static final int WEBSITE_MAX_LENGTH = 255;
    public static final int PHONE_MAX_LENGTH = 30;
}
