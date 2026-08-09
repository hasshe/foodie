package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserIconEntity;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.UserIconDisplay;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIconMapperImplTest {

    private final UserIconMapperImpl userIconMapperImpl = new UserIconMapperImpl();

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");

        UserIconDomain domain = userIconMapperImpl.mapToDomain(entity);

        assertThat(domain.iconKey()).isEqualTo("STAR");
        assertThat(domain.label()).isEqualTo("Star");
    }

    @Test
    void given_differentValidEntity_when_mapToDomain_then_returnsMatchingDomain() {
        UserIconEntity entity = new UserIconEntity("HEART", "Heart");

        UserIconDomain domain = userIconMapperImpl.mapToDomain(entity);

        assertThat(domain.iconKey()).isEqualTo("HEART");
        assertThat(domain.label()).isEqualTo("Heart");
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userIconMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        UserIconDomain domain = new UserIconDomain(1L, "STAR", "Star");

        UserIconDisplay display = userIconMapperImpl.mapToDisplay(domain);

        assertThat(display.id()).isEqualTo(1L);
        assertThat(display.iconKey()).isEqualTo("STAR");
        assertThat(display.label()).isEqualTo("Star");
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        UserIconDomain domain = new UserIconDomain(2L, "HEART", "Heart");

        UserIconDisplay display = userIconMapperImpl.mapToDisplay(domain);

        assertThat(display.id()).isEqualTo(2L);
        assertThat(display.iconKey()).isEqualTo("HEART");
        assertThat(display.label()).isEqualTo("Heart");
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userIconMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
