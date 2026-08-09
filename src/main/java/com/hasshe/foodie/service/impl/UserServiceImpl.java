package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.exception.ValidationException;
import com.hasshe.foodie.mapper.UserMapper;
import com.hasshe.foodie.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDb userDb;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(UserDb userDb, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userDb = userDb;
        this.userMapper = userMapper;
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
}
