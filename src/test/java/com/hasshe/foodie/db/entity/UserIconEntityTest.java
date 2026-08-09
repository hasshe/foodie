package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.UserIconConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIconEntityTest {

    @Test
    void given_validFields_when_construct_then_fieldsArePopulated() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");

        assertThat(entity.getIconKey()).isEqualTo("STAR");
        assertThat(entity.getLabel()).isEqualTo("Star");
    }

    @Test
    void given_differentValidFields_when_construct_then_fieldsArePopulated() {
        UserIconEntity entity = new UserIconEntity("HEART", "Heart");

        assertThat(entity.getIconKey()).isEqualTo("HEART");
        assertThat(entity.getLabel()).isEqualTo("Heart");
    }

    @Test
    void given_blankIconKey_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserIconEntity("   ", "Star"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankLabel_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserIconEntity("STAR", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullIconKey_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserIconEntity(null, "Star"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthIconKey_when_construct_then_accepted() {
        String maxLengthIconKey = "K".repeat(UserIconConstants.ICON_KEY_MAX_LENGTH);

        UserIconEntity entity = new UserIconEntity(maxLengthIconKey, "Star");

        assertThat(entity.getIconKey()).hasSize(UserIconConstants.ICON_KEY_MAX_LENGTH);
    }
}
