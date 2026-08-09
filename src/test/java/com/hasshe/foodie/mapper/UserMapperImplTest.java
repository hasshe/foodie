package com.hasshe.foodie.mapper;

import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserMapperImplTest {

    private final UserMapperImpl userMapperImpl = new UserMapperImpl();

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
}
