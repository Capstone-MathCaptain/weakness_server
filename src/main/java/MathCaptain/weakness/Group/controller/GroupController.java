package MathCaptain.weakness.Group.controller;

import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.service.RelationService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final RelationService relationService;

    @GetMapping("/group/{groupId}")
    public GroupResponseDto groupInfo(@PathVariable long groupId) {
        return groupService.getGroupInfo(groupId);
    }

    @PostMapping("/group")
    public String createGroup(@Valid @RequestBody GroupCreateRequestDto groupCreateRequestDto) {
        Long id = groupService.createGroup(groupCreateRequestDto);
        return id + "번 그룹이 생성되었습니다.";
    }

    @PutMapping("/group/{groupId}")
    public GroupResponseDto updateGroup(@PathVariable long groupId, @RequestBody GroupUpdateRequestDto groupUpdateRequestDto) {
        return groupService.updateGroupInfo(groupId, groupUpdateRequestDto);
    }

    @PostMapping("/group/join/{groupId}")
    public String joinGroup(@PathVariable long groupId, @RequestBody GroupJoinRequestDto groupJoinRequestDto) {
        groupService.joinGroup(groupId, groupJoinRequestDto);
        return "그룹에 가입되었습니다.";
    }

    @GetMapping("/group/members/{groupId}")
    public List<UserResponseDto> groupMembers(@PathVariable long groupId) {
        return groupService.getGroupMembers(groupId);
    }

    @GetMapping("/group/relation/{relationId}")
    public RelationResponseDto relationInfo(@PathVariable Long relationId) {
        return relationService.getRelationInfo(relationId);
    }
}
