package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface GroupJpaRepository extends JpaRepository<GroupEntity, Long> {

    @Query("SELECT g FROM GroupEntity g JOIN g.members m WHERE m.id = :userId")
    List<GroupEntity> findByMemberId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GroupEntity g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    boolean isMember(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
