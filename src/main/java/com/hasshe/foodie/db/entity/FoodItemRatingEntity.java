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

    @Column(name = FoodItemRatingConstants.COLUMN_TASTE, nullable = false)
    private int taste;

    @Column(name = FoodItemRatingConstants.COLUMN_PRESENTATION, nullable = false)
    private int presentation;

    @Column(name = FoodItemRatingConstants.COLUMN_PORTION_QUALITY, nullable = false)
    private int portionQuality;

    @Column(name = FoodItemRatingConstants.COLUMN_VALUE_FOR_PRICE, nullable = false)
    private int valueForPrice;

    @Column(name = FoodItemRatingConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = FoodItemRatingConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    protected FoodItemRatingEntity() {}

    public FoodItemRatingEntity(FoodItemEntity foodItem, UserEntity rater, int taste, int presentation, int portionQuality, int valueForPrice) {
        Assert.notNull(foodItem, "foodItem must not be null");
        Assert.notNull(rater, "rater must not be null");
        assertValidScore(taste, "taste");
        assertValidScore(presentation, "presentation");
        assertValidScore(portionQuality, "portionQuality");
        assertValidScore(valueForPrice, "valueForPrice");
        this.foodItem = foodItem;
        this.rater = rater;
        this.taste = taste;
        this.presentation = presentation;
        this.portionQuality = portionQuality;
        this.valueForPrice = valueForPrice;
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

    public void updateScores(int taste, int presentation, int portionQuality, int valueForPrice) {
        assertValidScore(taste, "taste");
        assertValidScore(presentation, "presentation");
        assertValidScore(portionQuality, "portionQuality");
        assertValidScore(valueForPrice, "valueForPrice");
        this.taste = taste;
        this.presentation = presentation;
        this.portionQuality = portionQuality;
        this.valueForPrice = valueForPrice;
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

    public int getTaste() {
        return taste;
    }

    public int getPresentation() {
        return presentation;
    }

    public int getPortionQuality() {
        return portionQuality;
    }

    public int getValueForPrice() {
        return valueForPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
