package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDisplay(
        @NotBlank @Size(max = UserConstants.USERNAME_MAX_LENGTH) String username,
        @NotBlank @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH) String password,
        @NotBlank @Size(max = UserConstants.DISPLAY_NAME_MAX_LENGTH) String displayName
) {}
