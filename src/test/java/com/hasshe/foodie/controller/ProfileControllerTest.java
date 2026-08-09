package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.ChangePasswordDisplay;
import com.hasshe.foodie.dto.UpdateProfileDisplay;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.hasshe.foodie.mapper.UserIconMapper;
import com.hasshe.foodie.mapper.UserMapper;
import com.hasshe.foodie.service.api.UserService;
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
class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserIconMapper userIconMapper;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void given_existingUsername_when_getProfile_then_returnsMappedDisplay() {
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", null, null, null);
        UserProfileDisplay display = new UserProfileDisplay("chef123", "Chef", null);
        when(userService.findByUsername("chef123")).thenReturn(Optional.of(domain));
        when(userMapper.mapToDisplay(domain)).thenReturn(display);

        Optional<UserProfileDisplay> result = profileController.getProfile("chef123");

        assertThat(result).contains(display);
    }

    @Test
    void given_unknownUsername_when_getProfile_then_returnsEmptyOptional() {
        when(userService.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<UserProfileDisplay> result = profileController.getProfile("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_getProfile_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> profileController.getProfile("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_icons_when_listAvailableIcons_then_returnsMappedDisplays() {
        UserIconDomain domain = new UserIconDomain(1L, "STAR", "Star");
        UserIconDisplay display = new UserIconDisplay(1L, "STAR", "Star");
        when(userService.listAvailableIcons()).thenReturn(List.of(domain));
        when(userIconMapper.mapToDisplay(domain)).thenReturn(display);

        List<UserIconDisplay> result = profileController.listAvailableIcons();

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noIcons_when_listAvailableIcons_then_returnsEmptyList() {
        when(userService.listAvailableIcons()).thenReturn(List.of());

        List<UserIconDisplay> result = profileController.listAvailableIcons();

        assertThat(result).isEmpty();
    }

    @Test
    void given_validRequest_when_updateProfile_then_returnsMappedDisplay() {
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Master Chef", null);
        UserDomain domain = new UserDomain(1L, "chef123", "Master Chef", null, null, null);
        UserProfileDisplay display = new UserProfileDisplay("chef123", "Master Chef", null);
        when(userService.updateProfile("chef123", request)).thenReturn(domain);
        when(userMapper.mapToDisplay(domain)).thenReturn(display);

        UserProfileDisplay result = profileController.updateProfile("chef123", request);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_updateProfile_then_callsServiceExactlyOnce() {
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Master Chef", null);
        UserDomain domain = new UserDomain(1L, "chef123", "Master Chef", null, null, null);
        when(userService.updateProfile("chef123", request)).thenReturn(domain);
        when(userMapper.mapToDisplay(domain)).thenReturn(new UserProfileDisplay("chef123", "Master Chef", null));

        profileController.updateProfile("chef123", request);

        verify(userService).updateProfile("chef123", request);
    }

    @Test
    void given_nullRequest_when_updateProfile_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> profileController.updateProfile("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankCurrentUsername_when_updateProfile_then_throwsIllegalArgumentException() {
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Chef", null);

        assertThatThrownBy(() -> profileController.updateProfile("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_validRequest_when_changePassword_then_delegatesToService() {
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword", "newPassword123");

        profileController.changePassword("chef123", request);

        verify(userService).changePassword("chef123", request);
    }

    @Test
    void given_anotherValidRequest_when_changePassword_then_delegatesToService() {
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword2", "newPassword456");

        profileController.changePassword("foodie99", request);

        verify(userService).changePassword("foodie99", request);
    }

    @Test
    void given_nullRequest_when_changePassword_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> profileController.changePassword("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_changePassword_then_throwsIllegalArgumentException() {
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword", "newPassword123");

        assertThatThrownBy(() -> profileController.changePassword("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
