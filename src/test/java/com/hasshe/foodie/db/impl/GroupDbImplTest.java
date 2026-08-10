package com.hasshe.foodie.db.impl;

import com.hasshe.foodie.db.entity.GroupEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupDbImplTest {

    @Mock
    private GroupJpaRepository groupJpaRepository;

    @InjectMocks
    private GroupDbImpl groupDbImpl;

    @Test
    void given_validEntity_when_save_then_returnsSavedEntity() {
        GroupEntity entity = new GroupEntity("Foodies");
        when(groupJpaRepository.save(entity)).thenReturn(entity);

        GroupEntity result = groupDbImpl.save(entity);

        assertThat(result).isEqualTo(entity);
    }

    @Test
    void given_anotherValidEntity_when_save_then_delegatesToRepository() {
        GroupEntity entity = new GroupEntity("Weekend Warriors");
        when(groupJpaRepository.save(entity)).thenReturn(entity);

        groupDbImpl.save(entity);

        verify(groupJpaRepository).save(entity);
    }

    @Test
    void given_nullEntity_when_save_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupDbImpl.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_existingId_when_findById_then_returnsEntity() {
        GroupEntity entity = new GroupEntity("Foodies");
        when(groupJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<GroupEntity> result = groupDbImpl.findById(1L);

        assertThat(result).contains(entity);
    }

    @Test
    void given_unknownId_when_findById_then_returnsEmptyOptional() {
        when(groupJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<GroupEntity> result = groupDbImpl.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullId_when_findById_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupDbImpl.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_userWithGroups_when_findByMemberId_then_returnsGroups() {
        GroupEntity entity = new GroupEntity("Foodies");
        when(groupJpaRepository.findByMemberId(1L)).thenReturn(List.of(entity));

        List<GroupEntity> result = groupDbImpl.findByMemberId(1L);

        assertThat(result).containsExactly(entity);
    }

    @Test
    void given_userWithNoGroups_when_findByMemberId_then_returnsEmptyList() {
        when(groupJpaRepository.findByMemberId(1L)).thenReturn(List.of());

        List<GroupEntity> result = groupDbImpl.findByMemberId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void given_nullUserId_when_findByMemberId_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupDbImpl.findByMemberId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_memberOfGroup_when_isMember_then_returnsTrue() {
        when(groupJpaRepository.isMember(1L, 2L)).thenReturn(true);

        boolean result = groupDbImpl.isMember(1L, 2L);

        assertThat(result).isTrue();
    }

    @Test
    void given_notMemberOfGroup_when_isMember_then_returnsFalse() {
        when(groupJpaRepository.isMember(1L, 2L)).thenReturn(false);

        boolean result = groupDbImpl.isMember(1L, 2L);

        assertThat(result).isFalse();
    }

    @Test
    void given_nullGroupId_when_isMember_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupDbImpl.isMember(null, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_nullUserId_when_isMember_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupDbImpl.isMember(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
