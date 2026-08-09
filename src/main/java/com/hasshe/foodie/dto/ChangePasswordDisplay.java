package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDisplay(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH) String newPassword
) {}
