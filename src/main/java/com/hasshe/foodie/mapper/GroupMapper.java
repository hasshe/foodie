package com.hasshe.foodie.mapper;

import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.GroupDisplay;

public interface GroupMapper {

    GroupDomain mapToDomain(GroupEntity groupEntity);

    GroupDisplay mapToDisplay(GroupDomain groupDomain);
}
