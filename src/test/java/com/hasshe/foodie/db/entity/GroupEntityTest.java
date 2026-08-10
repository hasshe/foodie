package com.hasshe.foodie.db.entity;

import com.hasshe.foodie.constants.GroupConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupEntityTest {

    @Test
    void given_validName_when_construct_then_nameIsPopulated() {
        GroupEntity entity = new GroupEntity("Foodies");

        assertThat(entity.getName()).isEqualTo("Foodies");
    }

    @Test
    void given_differentValidName_when_construct_then_nameIsPopulated() {
        GroupEntity entity = new GroupEntity("Weekend Warriors");

        assertThat(entity.getName()).isEqualTo("Weekend Warriors");
    }

    @Test
    void given_blankName_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new GroupEntity("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullName_when_construct_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> new GroupEntity(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthName_when_construct_then_accepted() {
        String maxLengthName = "G".repeat(GroupConstants.NAME_MAX_LENGTH);

        GroupEntity entity = new GroupEntity(maxLengthName);

        assertThat(entity.getName()).hasSize(GroupConstants.NAME_MAX_LENGTH);
    }

    @Test
    void given_validNewName_when_changeName_then_nameIsUpdated() {
        GroupEntity entity = new GroupEntity("Foodies");

        entity.changeName("Super Foodies");

        assertThat(entity.getName()).isEqualTo("Super Foodies");
    }

    @Test
    void given_anotherValidNewName_when_changeName_then_nameIsUpdated() {
        GroupEntity entity = new GroupEntity("Foodies");

        entity.changeName("Weekend Warriors");

        assertThat(entity.getName()).isEqualTo("Weekend Warriors");
    }

    @Test
    void given_blankName_when_changeName_then_throwsIllegalArgumentException() {
        GroupEntity entity = new GroupEntity("Foodies");

        assertThatThrownBy(() -> entity.changeName("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullUser_when_addMember_then_throwsIllegalArgumentException() {
        GroupEntity entity = new GroupEntity("Foodies");

        assertThatThrownBy(() -> entity.addMember(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validUser_when_addMember_then_doesNotThrow() {
        GroupEntity entity = new GroupEntity("Foodies");
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");

        entity.addMember(user);
    }
}
