package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.ChangePasswordDisplay;
import com.hasshe.foodie.dto.UpdateProfileDisplay;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import com.hasshe.foodie.mapper.UserIconMapper;
import com.hasshe.foodie.mapper.UserMapper;
import com.hasshe.foodie.service.api.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Component
public class ProfileController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserIconMapper userIconMapper;

    public ProfileController(UserService userService, UserMapper userMapper, UserIconMapper userIconMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userIconMapper = userIconMapper;
    }

    public Optional<UserProfileDisplay> getProfile(String username) {
        Assert.hasText(username, "username must not be blank");
        return userService.findByUsername(username).map(userMapper::mapToDisplay);
    }

    public List<UserIconDisplay> listAvailableIcons() {
        return userService.listAvailableIcons().stream().map(userIconMapper::mapToDisplay).toList();
    }

    public UserProfileDisplay updateProfile(String currentUsername, UpdateProfileDisplay updateProfileDisplay) {
        Assert.hasText(currentUsername, "currentUsername must not be blank");
        Assert.notNull(updateProfileDisplay, "updateProfileDisplay must not be null");
        UserDomain updatedUserDomain = userService.updateProfile(currentUsername, updateProfileDisplay);
        return userMapper.mapToDisplay(updatedUserDomain);
    }

    public void changePassword(String username, ChangePasswordDisplay changePasswordDisplay) {
        Assert.hasText(username, "username must not be blank");
        Assert.notNull(changePasswordDisplay, "changePasswordDisplay must not be null");
        userService.changePassword(username, changePasswordDisplay);
    }
}
