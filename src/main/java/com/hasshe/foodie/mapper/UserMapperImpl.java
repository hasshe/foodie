package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class UserMapperImpl implements UserMapper {

    @Override
    public UserDomain mapToDomain(UserEntity userEntity) {
        Assert.notNull(userEntity, "userEntity must not be null");
        UserDomain userDomain = new UserDomain(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getDisplayName(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
        assert userDomain != null : "mapping must never produce null";
        return userDomain;
    }
}
