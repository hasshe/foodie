package com.hasshe.foodie.controller;

import com.hasshe.foodie.domain.GroupDomain;
import com.hasshe.foodie.dto.CreateGroupDisplay;
import com.hasshe.foodie.dto.GroupDisplay;
import com.hasshe.foodie.mapper.GroupMapper;
import com.hasshe.foodie.service.api.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupMapper groupMapper;

    @InjectMocks
    private GroupController groupController;

    @Test
    void given_validRequest_when_createGroup_then_returnsMappedDisplay() {
        CreateGroupDisplay request = new CreateGroupDisplay("Foodies");
        GroupDomain domain = new GroupDomain(1L, "Foodies", null, null);
        GroupDisplay display = new GroupDisplay(1L, "Foodies");
        when(groupService.createGroup("chef123", request)).thenReturn(domain);
        when(groupMapper.mapToDisplay(domain)).thenReturn(display);

        GroupDisplay result = groupController.createGroup("chef123", request);

        assertThat(result).isEqualTo(display);
    }

    @Test
    void given_validRequest_when_createGroup_then_delegatesToServiceExactlyOnce() {
        CreateGroupDisplay request = new CreateGroupDisplay("Foodies");
        GroupDomain domain = new GroupDomain(1L, "Foodies", null, null);
        when(groupService.createGroup("chef123", request)).thenReturn(domain);
        when(groupMapper.mapToDisplay(domain)).thenReturn(new GroupDisplay(1L, "Foodies"));

        groupController.createGroup("chef123", request);

        verify(groupService).createGroup("chef123", request);
    }

    @Test
    void given_nullRequest_when_createGroup_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupController.createGroup("chef123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_blankCreatorUsername_when_createGroup_then_throwsIllegalArgumentException() {
        CreateGroupDisplay request = new CreateGroupDisplay("Foodies");

        assertThatThrownBy(() -> groupController.createGroup("  ", request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_groups_when_listGroupsForUser_then_returnsMappedDisplays() {
        GroupDomain domain = new GroupDomain(1L, "Foodies", null, null);
        GroupDisplay display = new GroupDisplay(1L, "Foodies");
        when(groupService.listGroupsForUser("chef123")).thenReturn(List.of(domain));
        when(groupMapper.mapToDisplay(domain)).thenReturn(display);

        List<GroupDisplay> result = groupController.listGroupsForUser("chef123");

        assertThat(result).containsExactly(display);
    }

    @Test
    void given_noGroups_when_listGroupsForUser_then_returnsEmptyList() {
        when(groupService.listGroupsForUser("chef123")).thenReturn(List.of());

        List<GroupDisplay> result = groupController.listGroupsForUser("chef123");

        assertThat(result).isEmpty();
    }

    @Test
    void given_blankUsername_when_listGroupsForUser_then_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> groupController.listGroupsForUser("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
