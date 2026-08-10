package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.mapper.GroupMapper;
import com.hasshe.foodie.service.api.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
class GroupServiceImpl implements GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupDb groupDb;
    private final UserDb userDb;
    private final GroupMapper groupMapper;

    GroupServiceImpl(GroupDb groupDb, UserDb userDb, GroupMapper groupMapper) {
        this.groupDb = groupDb;
        this.userDb = userDb;
        this.groupMapper = groupMapper;
    }

    @Override
    public GroupDomain createGroup(String creatorUsername, CreateGroupDisplay createGroupDisplay) {
        Assert.hasText(creatorUsername, "creatorUsername must not be blank");
        Assert.notNull(createGroupDisplay, "createGroupDisplay must not be null");
        log.debug("Creating group '{}' for username {}", createGroupDisplay.name(), creatorUsername);

        UserEntity creator = userDb.findByUsername(creatorUsername)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + creatorUsername));

        GroupEntity groupEntity = new GroupEntity(createGroupDisplay.name());
        groupEntity.addMember(creator);
        GroupEntity savedGroupEntity = groupDb.save(groupEntity);

        if (creator.getDefaultGroup().isEmpty()) {
            creator.changeDefaultGroup(savedGroupEntity);
            userDb.save(creator);
        }

        GroupDomain groupDomain = groupMapper.mapToDomain(savedGroupEntity);
        assert groupDomain != null : "mapper must never return null";
        log.info("Created group '{}' with id {} for username {}", groupDomain.name(), groupDomain.id(), creatorUsername);
        return groupDomain;
    }

    @Override
    public List<GroupDomain> listGroupsForUser(String username) {
        Assert.hasText(username, "username must not be blank");
        log.debug("Listing groups for username {}", username);

        UserEntity user = userDb.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No user found with username: " + username));

        return groupDb.findByMemberId(user.getId()).stream().map(groupMapper::mapToDomain).toList();
    }
}
