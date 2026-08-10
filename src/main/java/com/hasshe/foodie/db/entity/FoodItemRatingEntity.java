package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.FoodItemRatingConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Table(
        name = FoodItemRatingConstants.TABLE_FOOD_ITEM_RATINGS,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                FoodItemRatingConstants.COLUMN_FOOD_ITEM_ID,
                FoodItemRatingConstants.COLUMN_USER_ID
        })
)
public class FoodItemRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = FoodItemRatingConstants.COLUMN_ID)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = FoodItemRatingConstants.COLUMN_FOOD_ITEM_ID, nullable = false)
    private FoodItemEntity foodItem;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = FoodItemRatingConstants.COLUMN_USER_ID, nullable = false)
    private UserEntity rater;

    @Column(name = FoodItemRatingConstants.COLUMN_RATING, nullable = false)
    private int rating;

    @Column(name = FoodItemRatingConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = FoodItemRatingConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    protected FoodItemRatingEntity() {}

    public FoodItemRatingEntity(FoodItemEntity foodItem, UserEntity rater, int rating) {
        Assert.notNull(foodItem, "foodItem must not be null");
        Assert.notNull(rater, "rater must not be null");
        assertValidScore(rating, "rating");
        this.foodItem = foodItem;
        this.rater = rater;
        this.rating = rating;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateScore(int rating) {
        assertValidScore(rating, "rating");
        this.rating = rating;
    }

    private static void assertValidScore(int score, String fieldName) {
        Assert.isTrue(
                score >= FoodItemRatingConstants.MIN_SCORE && score <= FoodItemRatingConstants.MAX_SCORE,
                fieldName + " must be between " + FoodItemRatingConstants.MIN_SCORE + " and " + FoodItemRatingConstants.MAX_SCORE
        );
    }

    public Long getId() {
        return id;
    }

    public FoodItemEntity getFoodItem() {
        return foodItem;
    }

    public UserEntity getRater() {
        return rater;
    }

    public int getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
