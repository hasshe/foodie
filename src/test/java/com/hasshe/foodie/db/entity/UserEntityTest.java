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

    @Test
    void given_validNewUsername_when_changeUsername_then_usernameIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changeUsername("chefmaster");

        assertThat(entity.getUsername()).isEqualTo("chefmaster");
    }

    @Test
    void given_anotherValidNewUsername_when_changeUsername_then_usernameIsUpdated() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.changeUsername("foodie99");

        assertThat(entity.getUsername()).isEqualTo("foodie99");
    }

    @Test
    void given_blankUsername_when_changeUsername_then_throwsIllegalArgumentException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThatThrownBy(() -> entity.changeUsername("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_noIconSet_when_getUserIcon_then_returnsEmptyOptional() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThat(entity.getUserIcon()).isEmpty();
    }

    @Test
    void given_validIcon_when_changeUserIcon_then_getUserIconReturnsIt() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UserIconEntity iconEntity = new UserIconEntity("STAR", "Star");

        entity.changeUserIcon(iconEntity);

        assertThat(entity.getUserIcon()).contains(iconEntity);
    }

    @Test
    void given_anotherValidIcon_when_changeUserIcon_then_getUserIconReturnsIt() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UserIconEntity iconEntity = new UserIconEntity("HEART", "Heart");

        entity.changeUserIcon(iconEntity);

        assertThat(entity.getUserIcon()).contains(iconEntity);
    }

    @Test
    void given_nullIcon_when_changeUserIcon_then_throwsIllegalArgumentException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThatThrownBy(() -> entity.changeUserIcon(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_iconPreviouslySet_when_clearUserIcon_then_getUserIconReturnsEmptyOptional() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        entity.changeUserIcon(new UserIconEntity("STAR", "Star"));

        entity.clearUserIcon();

        assertThat(entity.getUserIcon()).isEmpty();
    }

    @Test
    void given_noIconSet_when_clearUserIcon_then_getUserIconStaysEmpty() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.clearUserIcon();

        assertThat(entity.getUserIcon()).isEmpty();
    }

    @Test
    void given_noDefaultGroupSet_when_getDefaultGroup_then_returnsEmptyOptional() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThat(entity.getDefaultGroup()).isEmpty();
    }

    @Test
    void given_validGroup_when_changeDefaultGroup_then_getDefaultGroupReturnsIt() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Foodies");

        entity.changeDefaultGroup(group);

        assertThat(entity.getDefaultGroup()).contains(group);
    }

    @Test
    void given_anotherValidGroup_when_changeDefaultGroup_then_getDefaultGroupReturnsIt() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity group = new GroupEntity("Weekend Warriors");

        entity.changeDefaultGroup(group);

        assertThat(entity.getDefaultGroup()).contains(group);
    }

    @Test
    void given_nullGroup_when_changeDefaultGroup_then_throwsIllegalArgumentException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        assertThatThrownBy(() -> entity.changeDefaultGroup(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_defaultGroupPreviouslySet_when_clearDefaultGroup_then_getDefaultGroupReturnsEmptyOptional() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        entity.changeDefaultGroup(new GroupEntity("Foodies"));

        entity.clearDefaultGroup();

        assertThat(entity.getDefaultGroup()).isEmpty();
    }

    @Test
    void given_noDefaultGroupSet_when_clearDefaultGroup_then_getDefaultGroupStaysEmpty() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.clearDefaultGroup();

        assertThat(entity.getDefaultGroup()).isEmpty();
    }
}
