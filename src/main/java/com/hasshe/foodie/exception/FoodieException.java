package com.hasshe.foodie.exception;

public abstract class FoodieException extends RuntimeException {

    protected FoodieException(String message) {
        super(message);
    }

    protected FoodieException(String message, Throwable cause) {
        super(message, cause);
    }
}
