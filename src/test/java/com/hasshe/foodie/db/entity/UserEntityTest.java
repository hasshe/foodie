package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.UserConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserEntityTest {

    @Test
    void given_validFields_when_construct_then_fieldsArePopulated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThat(entity.getUsername()).isEqualTo("chef123");
        assertThat(entity.getPassword()).isEqualTo("hashedPassword");
        assertThat(entity.getDisplayName()).isEqualTo("Chef");
    }

    @Test
    void given_differentValidFields_when_construct_then_fieldsArePopulated() {
        UserEntity entity = new UserEntity("foodie99", "anotherHash", "Foodie Fan");

        assertThat(entity.getUsername()).isEqualTo("foodie99");
        assertThat(entity.getPassword()).isEqualTo("anotherHash");
        assertThat(entity.getDisplayName()).isEqualTo("Foodie Fan");
    }

    @Test
    void given_blankUsername_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserEntity("   ", "hashedPassword", "Chef"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankPassword_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserEntity("chef123", "   ", "Chef"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullDisplayName_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new UserEntity("chef123", "hashedPassword", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthUsername_when_construct_then_accepted() {
        String maxLengthUsername = "u".repeat(UserConstants.USERNAME_MAX_LENGTH);

        UserEntity entity = new UserEntity(maxLengthUsername, "hashedPassword", "Chef");

        assertThat(entity.getUsername()).hasSize(UserConstants.USERNAME_MAX_LENGTH);
    }

    @Test
    void given_validNewPassword_when_changePassword_then_passwordIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changePassword("newHashedPassword");

        assertThat(entity.getPassword()).isEqualTo("newHashedPassword");
    }

    @Test
    void given_anotherValidNewPassword_when_changePassword_then_passwordIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changePassword("evenNewerHash");

        assertThat(entity.getPassword()).isEqualTo("evenNewerHash");
    }

    @Test
    void given_blankPassword_when_changePassword_then_throwsIllegalArgumentException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThatThrownBy(() -> entity.changePassword("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validNewDisplayName_when_changeDisplayName_then_displayNameIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changeDisplayName("Master Chef");

        assertThat(entity.getDisplayName()).isEqualTo("Master Chef");
    }

    @Test
    void given_anotherValidNewDisplayName_when_changeDisplayName_then_displayNameIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changeDisplayName("The Foodie");

        assertThat(entity.getDisplayName()).isEqualTo("The Foodie");
    }

    @Test
    void given_nullDisplayName_when_changeDisplayName_then_throwsIllegalArgumentException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThatThrownBy(() -> entity.changeDisplayName(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
