package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.constants.UserConstants;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.api.UserIconDb;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.db.entity.UserIconEntity;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.ChangePasswordDisplay;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.dto.UpdateProfileDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.UserIconMapper;
import com.hasshe.foodie.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDb userDb;

    @Mock
    private UserIconDb userIconDb;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserIconMapper userIconMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void given_newUsername_when_registerUser_then_returnsRegisteredDomain() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        UserEntity savedEntity = new UserEntity("chef123", "encodedPassword", "Chef");
        UserDomain expectedDomain = new UserDomain(1L, "chef123", "Chef", null, LocalDateTime.now(), LocalDateTime.now());

        when(userDb.existsByUsername("chef123")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userDb.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.mapToDomain(savedEntity)).thenReturn(expectedDomain);

        UserDomain result = userServiceImpl.registerUser(request);

        assertThat(result).isEqualTo(expectedDomain);
    }

    @Test
    void given_newUsername_when_registerUser_then_passwordIsEncodedBeforeSaving() {
        RegisterUserDisplay request = new RegisterUserDisplay("foodie99", "rawPassword", "Foodie");
        UserEntity savedEntity = new UserEntity("foodie99", "encodedPassword", "Foodie");

        when(userDb.existsByUsername("foodie99")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userDb.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.mapToDomain(savedEntity)).thenReturn(
                new UserDomain(2L, "foodie99", "Foodie", null, LocalDateTime.now(), LocalDateTime.now()));

        userServiceImpl.registerUser(request);

        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void given_existingUsername_when_registerUser_then_throwsValidationException() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        when(userDb.existsByUsername("chef123")).thenReturn(true);

        assertThatThrownBy(() -> userServiceImpl.registerUser(request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_existingUsername_when_registerUser_then_neverSaves() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        when(userDb.existsByUsername("chef123")).thenReturn(true);

        assertThatThrownBy(() -> userServiceImpl.registerUser(request))
                .isInstanceOf(ValidationException.class);
        verify(userDb, never()).save(any());
    }

    @Test
    void given_nullRegisterUserDisplay_when_registerUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userServiceImpl.registerUser(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthDisplayName_when_registerUser_then_registersSuccessfully() {
        String maxLengthDisplayName = "d".repeat(UserConstants.DISPLAY_NAME_MAX_LENGTH);
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", maxLengthDisplayName);
        UserEntity savedEntity = new UserEntity("chef123", "encodedPassword", maxLengthDisplayName);

        when(userDb.existsByUsername("chef123")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userDb.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.mapToDomain(savedEntity)).thenReturn(
                new UserDomain(3L, "chef123", maxLengthDisplayName, null, LocalDateTime.now(), LocalDateTime.now()));

        UserDomain result = userServiceImpl.registerUser(request);

        assertThat(result.displayName()).isEqualTo(maxLengthDisplayName);
    }

    @Test
    void given_newUsername_when_registerUser_then_savesExactlyOnce() {
        RegisterUserDisplay request = new RegisterUserDisplay("chef123", "rawPassword", "Chef");
        UserEntity savedEntity = new UserEntity("chef123", "encodedPassword", "Chef");

        when(userDb.existsByUsername("chef123")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userDb.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.mapToDomain(savedEntity)).thenReturn(
                new UserDomain(1L, "chef123", "Chef", null, LocalDateTime.now(), LocalDateTime.now()));

        userServiceImpl.registerUser(request);

        verify(userDb, times(1)).save(any(UserEntity.class));
    }

    @Test
    void given_existingUsername_when_findByUsername_then_returnsDomain() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UserDomain domain = new UserDomain(1L, "chef123", "Chef", null, LocalDateTime.now(), LocalDateTime.now());
        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userMapper.mapToDomain(entity)).thenReturn(domain);

        Optional<UserDomain> result = userServiceImpl.findByUsername("chef123");

        assertThat(result).contains(domain);
    }

    @Test
    void given_anotherExistingUsername_when_findByUsername_then_returnsDomain() {
        UserEntity entity = new UserEntity("foodie99", "hashedPassword", "Foodie");
        UserDomain domain = new UserDomain(2L, "foodie99", "Foodie", null, LocalDateTime.now(), LocalDateTime.now());
        when(userDb.findByUsername("foodie99")).thenReturn(Optional.of(entity));
        when(userMapper.mapToDomain(entity)).thenReturn(domain);

        Optional<UserDomain> result = userServiceImpl.findByUsername("foodie99");

        assertThat(result).contains(domain);
    }

    @Test
    void given_unknownUsername_when_findByUsername_then_returnsEmptyOptional() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<UserDomain> result = userServiceImpl.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullUsername_when_findByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userServiceImpl.findByUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankUsername_when_findByUsername_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userServiceImpl.findByUsername("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthUsername_when_findByUsername_then_delegatesToDb() {
        String maxLengthUsername = "u".repeat(UserConstants.USERNAME_MAX_LENGTH);
        when(userDb.findByUsername(maxLengthUsername)).thenReturn(Optional.empty());

        userServiceImpl.findByUsername(maxLengthUsername);

        verify(userDb).findByUsername(maxLengthUsername);
    }

    @Test
    void given_icons_when_listAvailableIcons_then_returnsMappedDomains() {
        UserIconEntity entity = new UserIconEntity("STAR", "Star");
        UserIconDomain domain = new UserIconDomain(1L, "STAR", "Star");
        when(userIconDb.findAll()).thenReturn(List.of(entity));
        when(userIconMapper.mapToDomain(entity)).thenReturn(domain);

        List<UserIconDomain> result = userServiceImpl.listAvailableIcons();

        assertThat(result).containsExactly(domain);
    }

    @Test
    void given_noIcons_when_listAvailableIcons_then_returnsEmptyList() {
        when(userIconDb.findAll()).thenReturn(List.of());

        List<UserIconDomain> result = userServiceImpl.listAvailableIcons();

        assertThat(result).isEmpty();
    }

    @Test
    void given_unchangedUsernameAndNewDisplayName_when_updateProfile_then_returnsUpdatedDomain() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Master Chef", null);
        UserDomain expected = new UserDomain(1L, "chef123", "Master Chef", null, LocalDateTime.now(), LocalDateTime.now());

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userDb.existsByUsernameAndIdNot("chef123", entity.getId())).thenReturn(false);
        when(userDb.save(entity)).thenReturn(entity);
        when(userMapper.mapToDomain(entity)).thenReturn(expected);

        UserDomain result = userServiceImpl.updateProfile("chef123", request);

        assertThat(result).isEqualTo(expected);
        assertThat(entity.getDisplayName()).isEqualTo("Master Chef");
    }

    @Test
    void given_validIconId_when_updateProfile_then_setsIconOnEntity() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UserIconEntity iconEntity = new UserIconEntity("STAR", "Star");
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Chef", 5L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userDb.existsByUsernameAndIdNot("chef123", entity.getId())).thenReturn(false);
        when(userIconDb.findById(5L)).thenReturn(Optional.of(iconEntity));
        when(userDb.save(entity)).thenReturn(entity);
        when(userMapper.mapToDomain(entity)).thenReturn(
                new UserDomain(1L, "chef123", "Chef", null, LocalDateTime.now(), LocalDateTime.now()));

        userServiceImpl.updateProfile("chef123", request);

        assertThat(entity.getUserIcon()).contains(iconEntity);
    }

    @Test
    void given_takenUsername_when_updateProfile_then_throwsValidationException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UpdateProfileDisplay request = new UpdateProfileDisplay("takenName", "Chef", null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userDb.existsByUsernameAndIdNot("takenName", entity.getId())).thenReturn(true);

        assertThatThrownBy(() -> userServiceImpl.updateProfile("chef123", request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void given_unknownIconId_when_updateProfile_then_throwsNotFoundException() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Chef", 99L);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userDb.existsByUsernameAndIdNot("chef123", entity.getId())).thenReturn(false);
        when(userIconDb.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.updateProfile("chef123", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_unknownCurrentUsername_when_updateProfile_then_throwsNotFoundException() {
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Chef", null);
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.updateProfile("ghost", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullUpdateProfileDisplay_when_updateProfile_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userServiceImpl.updateProfile("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullIconId_when_updateProfile_then_clearsExistingIcon() {
        UserEntity entity = new UserEntity("chef123", "hashedPassword", "Chef");
        entity.changeUserIcon(new UserIconEntity("STAR", "Star"));
        UpdateProfileDisplay request = new UpdateProfileDisplay("chef123", "Chef", null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(userDb.existsByUsernameAndIdNot("chef123", entity.getId())).thenReturn(false);
        when(userDb.save(entity)).thenReturn(entity);
        when(userMapper.mapToDomain(entity)).thenReturn(
                new UserDomain(1L, "chef123", "Chef", null, LocalDateTime.now(), LocalDateTime.now()));

        userServiceImpl.updateProfile("chef123", request);

        assertThat(entity.getUserIcon()).isEmpty();
    }

    @Test
    void given_correctCurrentPassword_when_changePassword_then_encodesAndSavesNewPassword() {
        UserEntity entity = new UserEntity("chef123", "encodedOldPassword", "Chef");
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword", "newPassword123");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        userServiceImpl.changePassword("chef123", request);

        assertThat(entity.getPassword()).isEqualTo("encodedNewPassword");
        verify(userDb).save(entity);
    }

    @Test
    void given_correctCurrentPassword_when_changePassword_then_savesExactlyOnce() {
        UserEntity entity = new UserEntity("foodie99", "encodedOldPassword", "Foodie");
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword", "newPassword123");

        when(userDb.findByUsername("foodie99")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        userServiceImpl.changePassword("foodie99", request);

        verify(userDb, times(1)).save(entity);
    }

    @Test
    void given_incorrectCurrentPassword_when_changePassword_then_throwsValidationException() {
        UserEntity entity = new UserEntity("chef123", "encodedOldPassword", "Chef");
        ChangePasswordDisplay request = new ChangePasswordDisplay("wrongPassword", "newPassword123");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        assertThatThrownBy(() -> userServiceImpl.changePassword("chef123", request))
                .isInstanceOf(ValidationException.class);
        verify(userDb, never()).save(any());
    }

    @Test
    void given_unknownUsername_when_changePassword_then_throwsNotFoundException() {
        ChangePasswordDisplay request = new ChangePasswordDisplay("oldPassword", "newPassword123");
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.changePassword("ghost", request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullChangePasswordDisplay_when_changePassword_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> userServiceImpl.changePassword("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
