package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.db.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDbImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserDbImpl userDbImpl;

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        when(userJpaRepository.save(entity)).thenReturn(entity);

        UserEntity result = userDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_validEntity_when_save_then_delegatesToRepository() {
        UserEntity entity = new UserEntity("foodie99", "hashedPassword", "Foodie");
        when(userJpaRepository.save(entity)).thenReturn(entity);

        userDbImpl.save(entity);

        verify(userJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_entityWithMaxLengthUsername_when_save_then_savesSuccessfully() {
        String maxLengthUsername = "u".repeat(UserConstants.USERNAME_MAX_LENGTH);
        UserEntity entity = new UserEntity(maxLengthUsername, "hashedPassword", "Display");
        when(userJpaRepository.save(entity)).thenReturn(entity);

        UserEntity result = userDbImpl.save(entity);

        assertThat(result.getUsername()).isEqualTo(maxLengthUsername);
    }

    @Test
    void given_existingId_when_findById_then_returnsEntity() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<UserEntity> result = userDbImpl.findById(1L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_differentExistingId_when_findById_then_returnsCorrespondingEntity() {
        UserEntity entity = new UserEntity("foodie99", "hashedPassword", "Foodie");
        when(userJpaRepository.findById(42L)).thenReturn(Optional.of(entity));

        Optional<UserEntity> result = userDbImpl.findById(42L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownId_when_findById_then_returnsEmptyOptional() {
        when(userJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userDbImpl.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_zeroId_when_findById_then_delegatesToRepository() {
        when(userJpaRepository.findById(0L)).thenReturn(Optional.empty());

        userDbImpl.findById(0L);

        verify(userJpaRepository).findById(0L);
    }

    @Test
    void given_negativeId_when_findById_then_delegatesToRepository() {
        when(userJpaRepository.findById(-1L)).thenReturn(Optional.empty());

        userDbImpl.findById(-1L);

        verify(userJpaRepository).findById(-1L);
    }

    @Test
    void given_existingUsername_when_findByUsername_then_returnsEntity() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        when(userJpaRepository.findByUsername("chef123")).thenReturn(Optional.of(entity));

        Optional<UserEntity> result = userDbImpl.findByUsername("chef123");

        assertThat(result).contains(entity);
    }

    @Test
    void given_anotherExistingUsername_when_findByUsername_then_returnsEntity() {
        UserEntity entity = new UserEntity("foodie99", "hashedPassword", "Foodie");
        when(userJpaRepository.findByUsername("foodie99")).thenReturn(Optional.of(entity));

        Optional<UserEntity> result = userDbImpl.findByUsername("foodie99");

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownUsername_when_findByUsername_then_returnsEmptyOptional() {
        when(userJpaRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<UserEntity> result = userDbImpl.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullUsername_when_findByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.findByUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_findByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.findByUsername("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthUsername_when_findByUsername_then_delegatesToRepository() {
        String maxLengthUsername = "u".repeat(UserConstants.USERNAME_MAX_LENGTH);
        when(userJpaRepository.findByUsername(maxLengthUsername)).thenReturn(Optional.empty());

        userDbImpl.findByUsername(maxLengthUsername);

        verify(userJpaRepository).findByUsername(maxLengthUsername);
    }

    @Test
    void given_existingUsername_when_existsByUsername_then_returnsTrue() {
        when(userJpaRepository.existsByUsername("chef123")).thenReturn(true);

        boolean result = userDbImpl.existsByUsername("chef123");

        assertThat(result).isTrue();
    }

    @Test
    void given_anotherExistingUsername_when_existsByUsername_then_returnsTrue() {
        when(userJpaRepository.existsByUsername("foodie99")).thenReturn(true);

        boolean result = userDbImpl.existsByUsername("foodie99");

        assertThat(result).isTrue();
    }

    @Test
    void given_unknownUsername_when_existsByUsername_then_returnsFalse() {
        when(userJpaRepository.existsByUsername("ghost")).thenReturn(false);

        boolean result = userDbImpl.existsByUsername("ghost");

        assertThat(result).isFalse();
    }

    @Test
    void given_nullUsername_when_existsByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.existsByUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_existsByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.existsByUsername(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_whitespaceOnlyUsername_when_existsByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userDbImpl.existsByUsername("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
