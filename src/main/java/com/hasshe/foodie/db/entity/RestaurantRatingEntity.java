package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.RestaurantRatingConstants;
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
        name = RestaurantRatingConstants.TABLE_RESTAURANT_RATINGS,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                RestaurantRatingConstants.COLUMN_RESTAURANT_ID,
                RestaurantRatingConstants.COLUMN_USER_ID
        })
)
public class RestaurantRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = RestaurantRatingConstants.COLUMN_ID)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = RestaurantRatingConstants.COLUMN_RESTAURANT_ID, nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = RestaurantRatingConstants.COLUMN_USER_ID, nullable = false)
    private UserEntity rater;

    @Column(name = RestaurantRatingConstants.COLUMN_FOOD, nullable = false)
    private int food;

    @Column(name = RestaurantRatingConstants.COLUMN_SERVICE, nullable = false)
    private int service;

    @Column(name = RestaurantRatingConstants.COLUMN_VIBE, nullable = false)
    private int vibe;

    @Column(name = RestaurantRatingConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = RestaurantRatingConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    protected RestaurantRatingEntity() {}

    public RestaurantRatingEntity(RestaurantEntity restaurant, UserEntity rater, int food, int service, int vibe) {
        Assert.notNull(restaurant, "restaurant must not be null");
        Assert.notNull(rater, "rater must not be null");
        assertValidScore(food, "food");
        assertValidScore(service, "service");
        assertValidScore(vibe, "vibe");
        this.restaurant = restaurant;
        this.rater = rater;
        this.food = food;
        this.service = service;
        this.vibe = vibe;
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

    public void updateScores(int food, int service, int vibe) {
        assertValidScore(food, "food");
        assertValidScore(service, "service");
        assertValidScore(vibe, "vibe");
        this.food = food;
        this.service = service;
        this.vibe = vibe;
    }

    private static void assertValidScore(int score, String fieldName) {
        Assert.isTrue(
                score >= RestaurantRatingConstants.MIN_SCORE && score <= RestaurantRatingConstants.MAX_SCORE,
                fieldName + " must be between " + RestaurantRatingConstants.MIN_SCORE + " and " + RestaurantRatingConstants.MAX_SCORE
        );
    }

    public Long getId() {
        return id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public UserEntity getRater() {
        return rater;
    }

    public int getFood() {
        return food;
    }

    public int getService() {
        return service;
    }

    public int getVibe() {
        return vibe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
