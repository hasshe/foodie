package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.UserConstants;
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
@Table(name = UserConstants.TABLE_USERS)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = UserConstants.COLUMN_ID)
    private Long id;

    @Column(name = UserConstants.COLUMN_USERNAME, nullable = false, unique = true, length = UserConstants.USERNAME_MAX_LENGTH)
    private String username;

    @Column(name = UserConstants.COLUMN_PASSWORD, nullable = false, length = UserConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @Column(name = UserConstants.COLUMN_DISPLAY_NAME, nullable = false, length = UserConstants.DISPLAY_NAME_MAX_LENGTH)
    private String displayName;

    @Column(name = UserConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = UserConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = UserConstants.COLUMN_USER_ICON_ID)
    private UserIconEntity userIcon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = UserConstants.COLUMN_DEFAULT_GROUP_ID)
    private GroupEntity defaultGroup;

    protected UserEntity() {}

    public UserEntity(String username, String password, String displayName) {
        Assert.hasText(username, "username must not be blank");
        Assert.hasText(password, "password must not be blank");
        Assert.hasText(displayName, "displayName must not be blank");
        this.username = username;
        this.password = password;
        this.displayName = displayName;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Optional<UserIconEntity> getUserIcon() {
        return Optional.ofNullable(userIcon);
    }

    public Optional<GroupEntity> getDefaultGroup() {
        return Optional.ofNullable(defaultGroup);
    }

    public void changeUsername(String newUsername) {
        Assert.hasText(newUsername, "newUsername must not be blank");
        this.username = newUsername;
    }

    public void changePassword(String newPassword) {
        Assert.hasText(newPassword, "newPassword must not be blank");
        this.password = newPassword;
    }

    public void changeDisplayName(String newDisplayName) {
        Assert.hasText(newDisplayName, "newDisplayName must not be blank");
        this.displayName = newDisplayName;
    }

    public void changeUserIcon(UserIconEntity newUserIcon) {
        Assert.notNull(newUserIcon, "newUserIcon must not be null");
        this.userIcon = newUserIcon;
    }

    public void clearUserIcon() {
        this.userIcon = null;
    }

    public void changeDefaultGroup(GroupEntity newDefaultGroup) {
        Assert.notNull(newDefaultGroup, "newDefaultGroup must not be null");
        this.defaultGroup = newDefaultGroup;
    }

    public void clearDefaultGroup() {
        this.defaultGroup = null;
    }
}
