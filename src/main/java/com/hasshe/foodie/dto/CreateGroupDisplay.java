package com.hasshe.foodie.dto;

import com.hasshe.foodie.constants.GroupConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupDisplay(
        @NotBlank @Size(max = GroupConstants.NAME_MAX_LENGTH) String name
) {}
