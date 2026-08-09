package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;

public interface UserMapper {

    UserDomain mapToDomain(UserEntity userEntity);
}
