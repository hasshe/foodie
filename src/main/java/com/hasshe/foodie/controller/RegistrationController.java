package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.RegisterUserDisplay;
import com.hasshe.foodie.service.api.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    public UserDomain registerUser(RegisterUserDisplay registerUserDisplay) {
        Assert.notNull(registerUserDisplay, "registerUserDisplay must not be null");
        return userService.registerUser(registerUserDisplay);
    }
}
