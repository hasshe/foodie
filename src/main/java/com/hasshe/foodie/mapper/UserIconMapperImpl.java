package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.UserIconEntity;
import com.hasshe.foodie.domain.UserIconDomain;
import com.hasshe.foodie.dto.UserIconDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class UserIconMapperImpl implements UserIconMapper {

    @Override
    public UserIconDomain mapToDomain(UserIconEntity userIconEntity) {
        Assert.notNull(userIconEntity, "userIconEntity must not be null");
        UserIconDomain userIconDomain = new UserIconDomain(
                userIconEntity.getId(),
                userIconEntity.getIconKey(),
                userIconEntity.getLabel()
        );
        assert userIconDomain != null : "mapping must never produce null";
        return userIconDomain;
    }

    @Override
    public UserIconDisplay mapToDisplay(UserIconDomain userIconDomain) {
        Assert.notNull(userIconDomain, "userIconDomain must not be null");
        UserIconDisplay userIconDisplay = new UserIconDisplay(
                userIconDomain.id(),
                userIconDomain.iconKey(),
                userIconDomain.label()
        );
        assert userIconDisplay != null : "mapping must never produce null";
        return userIconDisplay;
    }
}
