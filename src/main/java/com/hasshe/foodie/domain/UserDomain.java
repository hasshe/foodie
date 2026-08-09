package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record UserDomain(
        Long id,
        String username,
        String displayName,
        UserIconDomain userIcon,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
