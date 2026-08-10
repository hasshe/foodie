package com.hasshe.foodie.constants;

public final class UserConstants {

    private UserConstants() {}

    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_USER_ICON_ID = "user_icon_id";
    public static final String COLUMN_DEFAULT_GROUP_ID = "default_group_id";

    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 255;
    public static final int DISPLAY_NAME_MAX_LENGTH = 100;
}
