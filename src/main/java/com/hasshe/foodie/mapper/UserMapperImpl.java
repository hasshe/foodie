package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class UserMapperImpl implements UserMapper {

    private final UserIconMapper userIconMapper;

    UserMapperImpl(UserIconMapper userIconMapper) {
        this.userIconMapper = userIconMapper;
    }

    @Override
    public UserDomain mapToDomain(UserEntity userEntity) {
        Assert.notNull(userEntity, "userEntity must not be null");
        UserIconDomain userIconDomain = userEntity.getUserIcon()
                .map(userIconMapper::mapToDomain)
                .orElse(null);
        UserDomain userDomain = new UserDomain(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getDisplayName(),
                userIconDomain,
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );
        assert userDomain != null : "mapping must never produce null";
        return userDomain;
    }

    @Override
    public UserProfileDisplay mapToDisplay(UserDomain userDomain) {
        Assert.notNull(userDomain, "userDomain must not be null");
        UserIconDisplay userIconDisplay = userDomain.userIcon() == null
                ? null
                : userIconMapper.mapToDisplay(userDomain.userIcon());
        UserProfileDisplay userProfileDisplay = new UserProfileDisplay(
                userDomain.username(),
                userDomain.displayName(),
                userIconDisplay
        );
        assert userProfileDisplay != null : "mapping must never produce null";
        return userProfileDisplay;
    }
}
