package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.dto.UserProfileDisplay;

public interface UserMapper {

    UserDomain mapToDomain(UserEntity userEntity);

    UserProfileDisplay mapToDisplay(UserDomain userDomain);
}
