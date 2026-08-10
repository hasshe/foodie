package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.GroupDisplay;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GroupMapperImplTest {

    private final GroupMapperImpl groupMapperImpl = new GroupMapperImpl();

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        GroupEntity entity = new GroupEntity("Foodies");

        GroupDomain domain = groupMapperImpl.mapToDomain(entity);

        assertThat(domain.name()).isEqualTo("Foodies");
    }

    @Test
    void given_differentValidEntity_when_mapToDomain_then_returnsMatchingDomain() {
        GroupEntity entity = new GroupEntity("Weekend Warriors");

        GroupDomain domain = groupMapperImpl.mapToDomain(entity);

        assertThat(domain.name()).isEqualTo("Weekend Warriors");
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        GroupDomain domain = new GroupDomain(1L, "Foodies", null, null);

        GroupDisplay display = groupMapperImpl.mapToDisplay(domain);

        assertThat(display.id()).isEqualTo(1L);
        assertThat(display.name()).isEqualTo("Foodies");
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        GroupDomain domain = new GroupDomain(2L, "Weekend Warriors", null, null);

        GroupDisplay display = groupMapperImpl.mapToDisplay(domain);

        assertThat(display.id()).isEqualTo(2L);
        assertThat(display.name()).isEqualTo("Weekend Warriors");
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
