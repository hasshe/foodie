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

    @Column(name = RestaurantRatingConstants.COLUMN_EMPLOYEES_SERVICE, nullable = false)
    private int employeesService;

    @Column(name = RestaurantRatingConstants.COLUMN_AUDIO_MUSIC, nullable = false)
    private int audioMusic;

    @Column(name = RestaurantRatingConstants.COLUMN_GENERAL_VIBES, nullable = false)
    private int generalVibes;

    @Column(name = RestaurantRatingConstants.COLUMN_PRICE_FOR_QUALITY, nullable = false)
    private int priceForQuality;

    @Column(name = RestaurantRatingConstants.COLUMN_LOCATION_LOCALE, nullable = false)
    private int locationLocale;

    @Column(name = RestaurantRatingConstants.COLUMN_FOOD_QUALITY, nullable = false)
    private int foodQuality;

    @Column(name = RestaurantRatingConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = RestaurantRatingConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    protected RestaurantRatingEntity() {}

    public RestaurantRatingEntity(
            RestaurantEntity restaurant,
            UserEntity rater,
            int employeesService,
            int audioMusic,
            int generalVibes,
            int priceForQuality,
            int locationLocale,
            int foodQuality
    ) {
        Assert.notNull(restaurant, "restaurant must not be null");
        Assert.notNull(rater, "rater must not be null");
        assertValidScore(employeesService, "employeesService");
        assertValidScore(audioMusic, "audioMusic");
        assertValidScore(generalVibes, "generalVibes");
        assertValidScore(priceForQuality, "priceForQuality");
        assertValidScore(locationLocale, "locationLocale");
        assertValidScore(foodQuality, "foodQuality");
        this.restaurant = restaurant;
        this.rater = rater;
        this.employeesService = employeesService;
        this.audioMusic = audioMusic;
        this.generalVibes = generalVibes;
        this.priceForQuality = priceForQuality;
        this.locationLocale = locationLocale;
        this.foodQuality = foodQuality;
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

    public void updateScores(int employeesService, int audioMusic, int generalVibes, int priceForQuality, int locationLocale, int foodQuality) {
        assertValidScore(employeesService, "employeesService");
        assertValidScore(audioMusic, "audioMusic");
        assertValidScore(generalVibes, "generalVibes");
        assertValidScore(priceForQuality, "priceForQuality");
        assertValidScore(locationLocale, "locationLocale");
        assertValidScore(foodQuality, "foodQuality");
        this.employeesService = employeesService;
        this.audioMusic = audioMusic;
        this.generalVibes = generalVibes;
        this.priceForQuality = priceForQuality;
        this.locationLocale = locationLocale;
        this.foodQuality = foodQuality;
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

    public int getEmployeesService() {
        return employeesService;
    }

    public int getAudioMusic() {
        return audioMusic;
    }

    public int getGeneralVibes() {
        return generalVibes;
    }

    public int getPriceForQuality() {
        return priceForQuality;
    }

    public int getLocationLocale() {
        return locationLocale;
    }

    public int getFoodQuality() {
        return foodQuality;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
