package com.hasshe.foodie.service.impl;

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
import com.hasshe.foodie.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDb userDb;
    private final UserIconDb userIconDb;
    private final UserMapper userMapper;
    private final UserIconMapper userIconMapper;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(
            UserDb userDb,
            UserIconDb userIconDb,
            UserMapper userMapper,
            UserIconMapper userIconMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userDb = userDb;
        this.userIconDb = userIconDb;
        this.userMapper = userMapper;
        this.userIconMapper = userIconMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDomain registerUser(RegisterUserDisplay registerUserDisplay) {
        Assert.notNull(registerUserDisplay, "registerUserDisplay must not be null");
        log.debug("Registering user with username {}", registerUserDisplay.username());

        if (userDb.existsByUsername(registerUserDisplay.username())) {
            throw new ValidationException("Username already taken: " + registerUserDisplay.username());
        }

        String encodedPassword = passwordEncoder.encode(registerUserDisplay.password());
        UserEntity userEntity = new UserEntity(
                registerUserDisplay.username(),
                encodedPassword,
                registerUserDisplay.displayName()
        );
        UserEntity savedUserEntity = userDb.save(userEntity);

        UserDomain userDomain = userMapper.mapToDomain(savedUserEntity);
        assert userDomain != null : "mapper must never return null";
        log.info("Registered new user with username {}", userDomain.username());
        return userDomain;
    }

    @Override
    public Optional<UserDomain> findByUsername(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Finding user by username {}", username);
        return userDb.findByUsername(username).map(userMapper::mapToDomain);
    }

    @Override
    public List<UserIconDomain> listAvailableIcons() {
        log.debug("Listing available user icons");
        return userIconDb.findAll().stream().map(userIconMapper::mapToDomain).toList();
    }

    @Override
    public UserDomain updateProfile(String currentUsername, UpdateProfileDisplay updateProfileDisplay) {
        Assert.hasText(currentUsername, "currentUsername must not be blank");
        Assert.notNull(updateProfileDisplay, "updateProfileDisplay must not be null");
        log.debug("Updating profile for username {}", currentUsername);

        UserEntity userEntity = userDb.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + currentUsername));

        if (userDb.existsByUsernameAndIdNot(updateProfileDisplay.username(), userEntity.getId())) {
            throw new ValidationException("Username already taken: " + updateProfileDisplay.username());
        }

        userEntity.changeUsername(updateProfileDisplay.username());
        userEntity.changeDisplayName(updateProfileDisplay.displayName());

        if (updateProfileDisplay.iconId() == null) {
            userEntity.clearUserIcon();
        } else {
            UserIconEntity userIconEntity = userIconDb.findById(updateProfileDisplay.iconId())
                    .orElseThrow(() -> new NotFoundException("No icon found with id: " + updateProfileDisplay.iconId()));
            userEntity.changeUserIcon(userIconEntity);
        }

        UserEntity savedUserEntity = userDb.save(userEntity);
        UserDomain userDomain = userMapper.mapToDomain(savedUserEntity);
        assert userDomain != null : "mapper must never return null";
        log.info("Updated profile for username {}", userDomain.username());
        return userDomain;
    }

    @Override
    public void changePassword(String username, ChangePasswordDisplay changePasswordDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(changePasswordDisplay, "changePasswordDisplay must not be null");
        log.debug("Changing password for username {}", username);

        UserEntity userEntity = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        if (!passwordEncoder.matches(changePasswordDisplay.currentPassword(), userEntity.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        userEntity.changePassword(passwordEncoder.encode(changePasswordDisplay.newPassword()));
        userDb.save(userEntity);
        log.info("Changed password for username {}", username);
    }
}
