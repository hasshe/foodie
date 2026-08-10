package com.hasshe.foodie.service.api;

import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.CreateGroupDisplay;

import java.util.List;

public interface GroupService {

    GroupDomain createGroup(String creatorUsername, CreateGroupDisplay createGroupDisplay);

    List<GroupDomain> listGroupsForUser(String username);
}
