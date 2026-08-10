package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.mapper.GroupMapper;
import com.hasshe.foodie.service.api.GroupService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    public GroupController(GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
    }

    public GroupDisplay createGroup(String creatorUsername, CreateGroupDisplay createGroupDisplay) {
        Assert.hasText(creatorUsername, "creatorUsername must not be blank");
        Assert.notNull(createGroupDisplay, "createGroupDisplay must not be null");
        GroupDomain groupDomain = groupService.createGroup(creatorUsername, createGroupDisplay);
        return groupMapper.mapToDisplay(groupDomain);
    }

    public List<GroupDisplay> listGroupsForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        return groupService.listGroupsForUser(username).stream().map(groupMapper::mapToDisplay).toList();
    }
}
