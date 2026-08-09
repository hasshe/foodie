package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileDisplay(
        @NotBlank @Size(max = UserConstants.USERNAME_MAX_LENGTH) String username,
        @NotBlank @Size(max = UserConstants.DISPLAY_NAME_MAX_LENGTH) String displayName,
        Long iconId
) {}
