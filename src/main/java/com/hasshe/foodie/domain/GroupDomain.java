package com.hasshe.foodie.domain;

import java.time.LocalDateTime;

public record GroupDomain(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {}
