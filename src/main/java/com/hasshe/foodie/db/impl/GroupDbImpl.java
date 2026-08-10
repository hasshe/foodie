package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
class GroupDbImpl implements GroupDb {

    private static final Logger log = LoggerFactory.getLogger(GroupDbImpl.class);

    private final GroupJpaRepository groupJpaRepository;

    GroupDbImpl(GroupJpaRepository groupJpaRepository) {
        this.groupJpaRepository = groupJpaRepository;
    }

    @Override
    public GroupEntity save(GroupEntity groupEntity) {
        Assert.notNull(groupEntity, "groupEntity must not be null");
        log.debug("Saving group with name {}", groupEntity.getName());
        GroupEntity saved = groupJpaRepository.save(groupEntity);
        assert saved != null : "repository save must never return null";
        return saved;
    }

    @Override
    public Optional<GroupEntity> findById(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Finding group by id {}", id);
        return groupJpaRepository.findById(id);
    }

    @Override
    public List<GroupEntity> findByMemberId(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        log.debug("Finding groups for member with id {}", userId);
        return groupJpaRepository.findByMemberId(userId);
    }

    @Override
    public boolean isMember(Long groupId, Long userId) {
        Assert.notNull(groupId, "groupId must not be null");
        Assert.notNull(userId, "userId must not be null");
        log.debug("Checking membership of user {} in group {}", userId, groupId);
        return groupJpaRepository.isMember(groupId, userId);
    }
}
