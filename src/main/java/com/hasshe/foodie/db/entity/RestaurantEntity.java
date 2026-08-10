package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.RestaurantConstants;
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
import java.util.Optional;

@Entity
@Table(name = RestaurantConstants.TABLE_RESTAURANTS)
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = RestaurantConstants.COLUMN_ID)
    private Long id;

    @Column(name = RestaurantConstants.COLUMN_NAME, nullable = false, length = RestaurantConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(name = RestaurantConstants.COLUMN_ADDRESS, nullable = false, length = RestaurantConstants.ADDRESS_MAX_LENGTH)
    private String address;

    @Column(name = RestaurantConstants.COLUMN_CUISINE_TYPE, length = RestaurantConstants.CUISINE_TYPE_MAX_LENGTH)
    private String cuisineType;

    @Column(name = RestaurantConstants.COLUMN_WEBSITE, length = RestaurantConstants.WEBSITE_MAX_LENGTH)
    private String website;

    @Column(name = RestaurantConstants.COLUMN_PHONE, length = RestaurantConstants.PHONE_MAX_LENGTH)
    private String phone;

    @Column(name = RestaurantConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = RestaurantConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = RestaurantConstants.COLUMN_GROUP_ID, nullable = false)
    private GroupEntity group;

    protected RestaurantEntity() {}

    public RestaurantEntity(String name, String address, GroupEntity group, String cuisineType, String website, String phone) {
        Assert.hasText(name, "name must not be blank");
        Assert.hasText(address, "address must not be blank");
        Assert.notNull(group, "group must not be null");
        this.name = name;
        this.address = address;
        this.group = group;
        this.cuisineType = cuisineType;
        this.website = website;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Optional<String> getCuisineType() {
        return Optional.ofNullable(cuisineType);
    }

    public Optional<String> getWebsite() {
        return Optional.ofNullable(website);
    }

    public Optional<String> getPhone() {
        return Optional.ofNullable(phone);
    }

    public GroupEntity getGroup() {
        return group;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
