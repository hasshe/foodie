package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.domain.UserDomain;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.dto.UserIconDisplay;
import com.hasshe.foodie.dto.UserProfileDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class UserMapperImpl implements UserMapper {

    private final UserIconMapper userIconMapper;
    private final GroupMapper groupMapper;

    UserMapperImpl(UserIconMapper userIconMapper, GroupMapper groupMapper) {
        this.userIconMapper = userIconMapper;
        this.groupMapper = groupMapper;
    }

    @Override
    public UserDomain mapToDomain(UserEntity userEntity) {
        Assert.notNull(userEntity, "userEntity must not be null");
        UserIconDomain userIconDomain = userEntity.getUserIcon()
                .map(userIconMapper::mapToDomain)
                .orElse(null);
        GroupDomain defaultGroupDomain = userEntity.getDefaultGroup()
                .map(groupMapper::mapToDomain)
                .orElse(null);
        UserDomain userDomain = new UserDomain(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getDisplayName(),
                userIconDomain,
                defaultGroupDomain,
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
        GroupDisplay defaultGroupDisplay = userDomain.defaultGroup() == null
                ? null
                : groupMapper.mapToDisplay(userDomain.defaultGroup());
        UserProfileDisplay userProfileDisplay = new UserProfileDisplay(
                userDomain.username(),
                userDomain.displayName(),
                userIconDisplay,
                defaultGroupDisplay
        );
        assert userProfileDisplay != null : "mapping must never produce null";
        return userProfileDisplay;
    }
}
