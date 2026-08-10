package com.hasshe.foodie.mapper;

import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.db.entity.UserIconEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.UserProfileDisplay;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperImplTest {

    private final UserMapperImpl userMapperImpl = new UserMapperImpl(new UserIconMapperImpl(), new GroupMapperImpl());

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.username()).isEqualTo("chef123");
        assertThat(domain.displayName()).isEqualTo("Chef");
    }

    @Test
    void given_differentValidEntity_when_mapToDomain_then_returnsMatchingDomain() {
        UserEntity entity = new UserEntity("foodie99", "anotherHash", "Foodie Fan");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.username()).isEqualTo("foodie99");
        assertThat(domain.displayName()).isEqualTo("Foodie Fan");
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_entityWithoutPersistedId_when_mapToDomain_then_domainHasNullId() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.id()).isNull();
    }

    @Test
    void given_entityWithMaxLengthUsername_when_mapToDomain_then_preservesUsername() {
        String maxLengthUsername = "u".repeat(UserConstants.USERNAME_MAX_LENGTH);
        UserEntity entity = new UserEntity(maxLengthUsername, "hashedPassword", "Chef");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.username()).isEqualTo(maxLengthUsername);
    }

    @Test
    void given_entityWithIcon_when_mapToDomain_then_domainHasMappedIcon() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        entity.changeUserIcon(new UserIconEntity("STAR", "Star"));

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.userIcon()).isNotNull();
        assertThat(domain.userIcon().iconKey()).isEqualTo("STAR");
    }

    @Test
    void given_entityWithoutIcon_when_mapToDomain_then_domainHasNullIcon() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.userIcon()).isNull();
    }

    @Test
    void given_domainWithIcon_when_mapToDisplay_then_returnsMatchingDisplay() {
        UserIconDomain iconDomain = new UserIconDomain(1L, "STAR", "Star");
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", iconDomain, null, null, null);

        UserProfileDisplay display = userMapperImpl.mapToDisplay(domain);

        assertThat(display.username()).isEqualTo("chef123");
        assertThat(display.displayName()).isEqualTo("Chef");
        assertThat(display.userIcon().iconKey()).isEqualTo("STAR");
    }

    @Test
    void given_domainWithoutIcon_when_mapToDisplay_then_displayHasNullIcon() {
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", null, null, null, null);

        UserProfileDisplay display = userMapperImpl.mapToDisplay(domain);

        assertThat(display.userIcon()).isNull();
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_entityWithDefaultGroup_when_mapToDomain_then_domainHasMappedDefaultGroup() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        entity.changeDefaultGroup(new GroupEntity("Foodies"));

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.defaultGroup()).isNotNull();
        assertThat(domain.defaultGroup().name()).isEqualTo("Foodies");
    }

    @Test
    void given_entityWithoutDefaultGroup_when_mapToDomain_then_domainHasNullDefaultGroup() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");

        UserDomain domain = userMapperImpl.mapToDomain(entity);

        assertThat(domain.defaultGroup()).isNull();
    }

    @Test
    void given_domainWithDefaultGroup_when_mapToDisplay_then_returnsMatchingDefaultGroup() {
        GroupDomain groupDomain = new GroupDomain(1L, "Foodies", null, null);
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", null, groupDomain, null, null);

        UserProfileDisplay display = userMapperImpl.mapToDisplay(domain);

        assertThat(display.defaultGroup()).isNotNull();
        assertThat(display.defaultGroup().name()).isEqualTo("Foodies");
    }

    @Test
    void given_domainWithoutDefaultGroup_when_mapToDisplay_then_displayHasNullDefaultGroup() {
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", null, null, null, null);

        UserProfileDisplay display = userMapperImpl.mapToDisplay(domain);

        assertThat(display.defaultGroup()).isNull();
    }
}
