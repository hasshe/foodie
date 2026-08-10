package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.GroupConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = GroupConstants.TABLE_GROUPS)
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = GroupConstants.COLUMN_ID)
    private Long id;

    @Column(name = GroupConstants.COLUMN_NAME, nullable = false, length = GroupConstants.NAME_MAX_LENGTH)
    private String name;

    @Column(name = GroupConstants.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = GroupConstants.COLUMN_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = GroupConstants.TABLE_GROUP_MEMBERS,
            joinColumns = @JoinColumn(name = GroupConstants.COLUMN_GROUP_ID),
            inverseJoinColumns = @JoinColumn(name = GroupConstants.COLUMN_USER_ID)
    )
    private Set<UserEntity> members = new HashSet<>();

    protected GroupEntity() {}

    public GroupEntity(String name) {
        Assert.hasText(name, "name must not be blank");
        this.name = name;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeName(String newName) {
        Assert.hasText(newName, "newName must not be blank");
        this.name = newName;
    }

    public void addMember(UserEntity user) {
        Assert.notNull(user, "user must not be null");
        members.add(user);
    }
}
