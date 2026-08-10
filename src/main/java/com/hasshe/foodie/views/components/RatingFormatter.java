package com.hasshe.foodie.views.components;

import java.util.Locale;

public class RatingFormatter {

    public String format(double score) {
        return String.format(Locale.US, "%.1f", score);
    }

    public String format(double averageRating, int ratingCount) {
        return ratingCount == 0 ? "No ratings" : format(averageRating);
    }
}
