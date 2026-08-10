package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.RestaurantEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.RestaurantDomain;
import com.hasshe.foodie.dto.RestaurantDisplay;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestaurantMapperImplTest {

    private final RestaurantMapperImpl restaurantMapperImpl = new RestaurantMapperImpl(new GroupMapperImpl());

    @Test
    void given_validEntity_when_mapToDomain_then_returnsMatchingDomain() {
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, "American", "https://diner.example", "555-1234");

        RestaurantDomain domain = restaurantMapperImpl.mapToDomain(entity);

        assertThat(domain.name()).isEqualTo("The Diner");
        assertThat(domain.address()).isEqualTo("123 Main St");
        assertThat(domain.cuisineType()).isEqualTo("American");
        assertThat(domain.website()).isEqualTo("https://diner.example");
        assertThat(domain.phone()).isEqualTo("555-1234");
        assertThat(domain.group().name()).isEqualTo("Foodies");
    }

    @Test
    void given_entityWithoutOptionalFields_when_mapToDomain_then_domainHasNullOptionalFields() {
        GroupEntity group = new GroupEntity("Foodies");
        RestaurantEntity entity = new RestaurantEntity("The Diner", "123 Main St", group, null, null, null);

        RestaurantDomain domain = restaurantMapperImpl.mapToDomain(entity);

        assertThat(domain.cuisineType()).isNull();
        assertThat(domain.website()).isNull();
        assertThat(domain.phone()).isNull();
    }

    @Test
    void given_nullEntity_when_mapToDomain_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantMapperImpl.mapToDomain(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        GroupDomain groupDomain = new GroupDomain(1L, "Foodies", null, null);
        RestaurantDomain domain = new RestaurantDomain(
                1L, "The Diner", "123 Main St", "American", "https://diner.example", "555-1234", groupDomain, null, null
        );

        RestaurantDisplay display = restaurantMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("The Diner");
        assertThat(display.groupName()).isEqualTo("Foodies");
    }

    @Test
    void given_anotherValidDomain_when_mapToDisplay_then_returnsMatchingDisplay() {
        GroupDomain groupDomain = new GroupDomain(2L, "Weekend Warriors", null, null);
        RestaurantDomain domain = new RestaurantDomain(
                2L, "Pizza Place", "456 Oak Ave", null, null, null, groupDomain, null, null
        );

        RestaurantDisplay display = restaurantMapperImpl.mapToDisplay(domain);

        assertThat(display.name()).isEqualTo("Pizza Place");
        assertThat(display.groupName()).isEqualTo("Weekend Warriors");
    }

    @Test
    void given_nullDomain_when_mapToDisplay_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> restaurantMapperImpl.mapToDisplay(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
