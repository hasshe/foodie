package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.UserIconConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.util.Assert;

@Entity
@Table(name = UserIconConstants.TABLE_USER_ICONS)
public class UserIconEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = UserIconConstants.COLUMN_ID)
    private Long id;

    @Column(name = UserIconConstants.COLUMN_ICON_KEY, nullable = false, unique = true, length = UserIconConstants.ICON_KEY_MAX_LENGTH)
    private String iconKey;

    @Column(name = UserIconConstants.COLUMN_LABEL, nullable = false, length = UserIconConstants.LABEL_MAX_LENGTH)
    private String label;

    protected UserIconEntity() {}

    public UserIconEntity(String iconKey, String label) {
        Assert.hasText(iconKey, "iconKey must not be blank");
        Assert.hasText(label, "label must not be blank");
        this.iconKey = iconKey;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public String getIconKey() {
        return iconKey;
    }

    public String getLabel() {
        return label;
    }
}
