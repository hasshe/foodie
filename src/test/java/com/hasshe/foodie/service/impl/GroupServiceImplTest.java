package com.hasshe.foodie.service.impl;

import com.hasshe.foodie.db.api.GroupDb;
import com.hasshe.foodie.db.api.UserDb;
import com.hasshe.foodie.db.entity.GroupEntity;
import com.hasshe.foodie.db.entity.UserEntity;
import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.exception.NotFoundException;
import com.hasshe.foodie.mapper.GroupMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupDb groupDb;

    @Mock
    private UserDb userDb;

    @Mock
    private GroupMapper groupMapper;

    @InjectMocks
    private GroupServiceImpl groupServiceImpl;

    @Test
    void given_validRequest_when_createGroup_then_returnsCreatedDomain() {
        UserEntity creator = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity savedEntity = new GroupEntity("Foodies");
        GroupDomain expected = new GroupDomain(1L, "Foodies", null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(creator));
        when(groupDb.save(any(GroupEntity.class))).thenReturn(savedEntity);
        when(groupMapper.mapToDomain(savedEntity)).thenReturn(expected);

        GroupDomain result = groupServiceImpl.createGroup("chef123", new CreateGroupDisplay("Foodies"));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void given_validRequest_when_createGroup_then_savesExactlyOnce() {
        UserEntity creator = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity savedEntity = new GroupEntity("Foodies");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(creator));
        when(groupDb.save(any(GroupEntity.class))).thenReturn(savedEntity);
        when(groupMapper.mapToDomain(savedEntity)).thenReturn(new GroupDomain(1L, "Foodies", null, null));

        groupServiceImpl.createGroup("chef123", new CreateGroupDisplay("Foodies"));

        verify(groupDb, times(1)).save(any(GroupEntity.class));
    }

    @Test
    void given_unknownCreator_when_createGroup_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupServiceImpl.createGroup("ghost", new CreateGroupDisplay("Foodies")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_nullCreateGroupDisplay_when_createGroup_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupServiceImpl.createGroup("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankCreatorUsername_when_createGroup_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupServiceImpl.createGroup("  ", new CreateGroupDisplay("Foodies")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_maxLengthGroupName_when_createGroup_then_createsSuccessfully() {
        UserEntity creator = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity savedEntity = new GroupEntity("Foodies");
        String maxLengthName = "G".repeat(100);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(creator));
        when(groupDb.save(any(GroupEntity.class))).thenReturn(savedEntity);
        when(groupMapper.mapToDomain(savedEntity)).thenReturn(new GroupDomain(1L, maxLengthName, null, null));

        GroupDomain result = groupServiceImpl.createGroup("chef123", new CreateGroupDisplay(maxLengthName));

        assertThat(result.name()).isEqualTo(maxLengthName);
    }

    @Test
    void given_creatorWithNoDefaultGroup_when_createGroup_then_automaticallySetsNewGroupAsDefault() {
        UserEntity creator = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity savedEntity = new GroupEntity("Foodies");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(creator));
        when(groupDb.save(any(GroupEntity.class))).thenReturn(savedEntity);
        when(groupMapper.mapToDomain(savedEntity)).thenReturn(new GroupDomain(1L, "Foodies", null, null));

        groupServiceImpl.createGroup("chef123", new CreateGroupDisplay("Foodies"));

        assertThat(creator.getDefaultGroup()).contains(savedEntity);
        verify(userDb).save(creator);
    }

    @Test
    void given_creatorWithExistingDefaultGroup_when_createGroup_then_doesNotOverrideDefault() {
        UserEntity creator = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity existingDefault = new GroupEntity("Weekend Warriors");
        creator.changeDefaultGroup(existingDefault);
        GroupEntity newGroup = new GroupEntity("Foodies");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(creator));
        when(groupDb.save(any(GroupEntity.class))).thenReturn(newGroup);
        when(groupMapper.mapToDomain(newGroup)).thenReturn(new GroupDomain(2L, "Foodies", null, null));

        groupServiceImpl.createGroup("chef123", new CreateGroupDisplay("Foodies"));

        assertThat(creator.getDefaultGroup()).contains(existingDefault);
        verify(userDb, never()).save(creator);
    }

    @Test
    void given_userWithGroups_when_listGroupsForUser_then_returnsMappedGroups() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");
        GroupEntity groupEntity = new GroupEntity("Foodies");
        GroupDomain groupDomain = new GroupDomain(1L, "Foodies", null, null);

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of(groupEntity));
        when(groupMapper.mapToDomain(groupEntity)).thenReturn(groupDomain);

        List<GroupDomain> result = groupServiceImpl.listGroupsForUser("chef123");

        assertThat(result).containsExactly(groupDomain);
    }

    @Test
    void given_userWithNoGroups_when_listGroupsForUser_then_returnsEmptyList() {
        UserEntity user = new UserEntity("chef123", "hashedPassword", "Chef");

        when(userDb.findByUsername("chef123")).thenReturn(Optional.of(user));
        when(groupDb.findByMemberId(user.getId())).thenReturn(List.of());

        List<GroupDomain> result = groupServiceImpl.listGroupsForUser("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_unknownUsername_when_listGroupsForUser_then_throwsNotFoundException() {
        when(userDb.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupServiceImpl.listGroupsForUser("ghost"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void given_blankUsername_when_listGroupsForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupServiceImpl.listGroupsForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
