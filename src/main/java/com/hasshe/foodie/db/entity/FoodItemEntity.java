package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.FoodItemConstants;
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
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Table(name = FoodItemConstants.TABLE_FOOD_ITEMS)
public class FoodItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = FoodItemConstants.COLUMN_ID)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = FoodItemConstants.COLUMN_RESTAURANT_ID, nullable = false)
    private RestaurantEntity restaurant;

    @Column(name = FoodItemConstants.COLUMN_NAME, nullable = false, length = FoodItemConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(name = FoodItemConstants.COLUMN_DISH_CATEGORY, nullable = false, length = FoodItemConstants.DISH_CATEGORY_MAX_LENGTH)
    private String dishCategory;

    @Column(name = FoodItemConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = FoodItemConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    protected FoodItemEntity() {}

    public FoodItemEntity(RestaurantEntity restaurant, String name, String dishCategory) {
        Assert.notNull(restaurant, "restaurant must not be null");
        Assert.hasText(name, "name must not be blank");
        Assert.hasText(dishCategory, "dishCategory must not be blank");
        this.restaurant = restaurant;
        this.name = name;
        this.dishCategory = dishCategory;
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

    public Long getId() {
        return id;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }

    public String getDishCategory() {
        return dishCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
