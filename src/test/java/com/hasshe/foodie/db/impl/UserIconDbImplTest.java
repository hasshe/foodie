package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.UserIconEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserIconDbImplTest {

    @Mock
    private UserIconJpaRepository userIconJpaRepository;

    @InjectMocks
    private UserIconDbImpl userIconDbImpl;

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");
        when(userIconJpaRepository.save(entity)).thenReturn(entity);

        UserIconEntity result = userIconDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        UserIconEntity entity = new UserIconEntity("HEART", "Heart");
        when(userIconJpaRepository.save(entity)).thenReturn(entity);

        userIconDbImpl.save(entity);

        verify(userIconJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userIconDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_icons_when_findAll_then_returnsAllIcons() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");
        when(userIconJpaRepository.findAll()).thenReturn(List.of(entity));

        List<UserIconEntity> result = userIconDbImpl.findAll();

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_noIcons_when_findAll_then_returnsEmptyList() {
        when(userIconJpaRepository.findAll()).thenReturn(List.of());

        List<UserIconEntity> result = userIconDbImpl.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void given_existingId_when_findById_then_returnsEntity() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");
        when(userIconJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<UserIconEntity> result = userIconDbImpl.findById(1L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownId_when_findById_then_returnsEmptyOptional() {
        when(userIconJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserIconEntity> result = userIconDbImpl.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userIconDbImpl.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
