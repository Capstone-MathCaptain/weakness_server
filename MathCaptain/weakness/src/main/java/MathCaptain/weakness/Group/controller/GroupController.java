package MathCaptain.weakness.Group.controller;

import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.service.RelationService;
import MathCaptain.weakness.global.Api.ApiResponse;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final RelationService relationService;

    // READ
    @GetMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> groupInfo(@PathVariable long groupId) {
        return groupService.getGroupInfo(groupId);
    }

    // CREATE
    @PostMapping("/group")
    public ApiResponse<GroupResponseDto> createGroup(@Valid @RequestBody GroupCreateRequestDto groupCreateRequestDto) {
        return groupService.createGroup(groupCreateRequestDto);
    }

    // UPDATE
    @PutMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> updateGroup(@Valid @PathVariable long groupId, @RequestBody GroupUpdateRequestDto groupUpdateRequestDto) {
        return groupService.updateGroupInfo(groupId, groupUpdateRequestDto);
    }

    // JOIN
    @PostMapping("/group/join/{groupId}")
    public ApiResponse<?> joinGroup(@Valid @PathVariable long groupId, @RequestBody GroupJoinRequestDto groupJoinRequestDto) {
        return groupService.joinGroup(groupId, groupJoinRequestDto);
    }

    // READ
    @GetMapping("/group/members/{groupId}")
    public ApiResponse<List<UserResponseDto>> groupMembers(@PathVariable long groupId) {
        return groupService.getGroupMembers(groupId);
    }

    // READ
    @GetMapping("/group/relation/{relationId}")
    public RelationResponseDto relationInfo(@PathVariable Long relationId) {
        return relationService.getRelationInfo(relationId);
    }

    // 유저가 속한 그룹을 모두 보여줌
    @GetMapping("/group")
    public void getUsersGroups() {

    }
}
