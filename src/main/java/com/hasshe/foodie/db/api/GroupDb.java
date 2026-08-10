package com.hasshe.foodie.db.api;

import com.hasshe.foodie.db.entity.GroupEntity;

import java.util.List;
import java.util.Optional;

public interface GroupDb {

    GroupEntity save(GroupEntity groupEntity);

    Optional<GroupEntity> findById(Long id);

    List<GroupEntity> findByMemberId(Long userId);

    boolean isMember(Long groupId, Long userId);
}
