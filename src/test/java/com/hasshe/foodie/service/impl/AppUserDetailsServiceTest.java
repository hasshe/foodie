package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.constants.SecurityConstants;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserDb userDb;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    void given_existingUsername_when_loadUserByUsername_then_returnsUserDetailsWithMatchingUsername() {
        UserEntity entity = new UserEntity("chef123", "encodedPassword", "Chef");
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));

        UserDetails result = appUserDetailsService.loadUserByUsername("chef123");

        assertThat(result.getUsername()).isEqualTo("chef123");
    }

    @Test
    void given_existingUsername_when_loadUserByUsername_then_returnsUserDetailsWithEncodedPassword() {
        UserEntity entity = new UserEntity("chef123", "encodedPassword", "Chef");
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));

        UserDetails result = appUserDetailsService.loadUserByUsername("chef123");

        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void given_unknownUsername_when_loadUserByUsername_then_throwsUsernameNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void given_nullUsername_when_loadUserByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_loadUserByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingUsername_when_loadUserByUsername_then_grantsUserAuthority() {
        UserEntity entity = new UserEntity("chef123", "encodedPassword", "Chef");
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));

        UserDetails result = appUserDetailsService.loadUserByUsername("chef123");

        assertThat(result.getAuthorities())
                .extracting(Object::toString)
                .containsExactly(SecurityConstants.ROLE_USER);
    }
}
