package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserIconEntity;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.UserIconDisplay;

public interface UserIconMapper {

    UserIconDomain mapToDomain(UserIconEntity userIconEntity);

    UserIconDisplay mapToDisplay(UserIconDomain userIconDomain);
}
