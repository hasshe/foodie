package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.GroupDisplay;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class GroupMapperImpl implements GroupMapper {

    @Override
    public GroupDomain mapToDomain(GroupEntity groupEntity) {
        Assert.notNull(groupEntity, "groupEntity must not be null");
        GroupDomain groupDomain = new GroupDomain(
                groupEntity.getId(),
                groupEntity.getName(),
                groupEntity.getCreatedAt(),
                groupEntity.getUpdatedAt()
        );
        assert groupDomain != null : "mapping must never produce null";
        return groupDomain;
    }

    @Override
    public GroupDisplay mapToDisplay(GroupDomain groupDomain) {
        Assert.notNull(groupDomain, "groupDomain must not be null");
        GroupDisplay groupDisplay = new GroupDisplay(groupDomain.id(), groupDomain.name());
        assert groupDisplay != null : "mapping must never produce null";
        return groupDisplay;
    }
}
