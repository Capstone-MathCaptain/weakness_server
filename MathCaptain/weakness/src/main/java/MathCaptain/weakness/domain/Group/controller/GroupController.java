package MathCaptain.weakness.domain.Group.controller;

import MathCaptain.weakness.domain.Group.dto.request.GroupSearchRequestDto;
import MathCaptain.weakness.domain.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.domain.Group.dto.response.GroupDetailResponseDto;
import MathCaptain.weakness.domain.Group.dto.response.GroupMemberListResponseDto;
import MathCaptain.weakness.domain.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.domain.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import MathCaptain.weakness.domain.Group.service.GroupService;
import MathCaptain.weakness.domain.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.domain.Group.service.RelationService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final RelationService relationService;

    /// 그룹 CRUD

    // 유저가 속한 그룹을 모두 보여줌
    @GetMapping("/group")
    public ApiResponse<List<GroupResponseDto>> getUsersGroups(@LoginUser Users loginUser) {
        return groupService.getUsersGroups(loginUser);
    }

    // 그룹 조회
    @GetMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> groupInfo(@PathVariable Long groupId) {
        return ApiResponse.ok(groupService.getGroupInfo(groupId));
    }

    // 그룹 생성
    @PostMapping("/group")
    public ApiResponse<GroupResponseDto> createGroup(@Valid @LoginUser Users loginUser,
                                                     @RequestBody GroupCreateRequestDto groupCreateRequestDto, HttpServletResponse response) {
        return groupService.createGroup(loginUser ,groupCreateRequestDto, response);
    }

    // 그룹 정보 수정
    @PutMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> updateGroup(@Valid @PathVariable Long groupId, @RequestBody GroupUpdateRequestDto groupUpdateRequestDto) {
        return groupService.updateGroupInfo(groupId, groupUpdateRequestDto);
    }

    // 그룹 삭제
    @DeleteMapping("/group/{groupId}")
    public ApiResponse<?> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }

    /// 조회

    // 그룹 멤버 조회
    @GetMapping("/group/members/{groupId}")
    public ApiResponse<List<UserResponse>> groupMembers(@PathVariable Long groupId) {
        return ApiResponse.ok(groupService.getGroupMembers(groupId));
    }

    // 그룹 관계 조회
    @GetMapping("/group/relation/{relationId}")
    public RelationResponseDto relationInfo(@PathVariable Long relationId) {
        return relationService.getRelationInfo(relationId);
    }

    // 그룹 상세 정보
    @GetMapping("/group/detail/{groupId}")
    public ApiResponse<GroupDetailResponseDto> groupDetail(@PathVariable Long groupId) {
        return groupService.getGroupDetail(groupId);
    }

    // 그룹 멤버 리스트 (그룹 상세 페이지)
    @GetMapping("/group/detail/{groupId}/members")
    public ApiResponse<List<GroupMemberListResponseDto>> GroupMemberList(@PathVariable Long groupId) {
        return ApiResponse.ok(relationService.getGroupMemberList(groupId));
    }

    // 그룹 검색 (그룹명)
    @PostMapping("/group/search")
    public ApiResponse<List<GroupResponseDto>> searchGroup(@RequestBody GroupSearchRequestDto groupSearchRequestDto) {
        return groupService.getGroupInfo(groupSearchRequestDto.getGroupName());
    }

    @GetMapping("/group/total")
    public ApiResponse<?> getGroups(@RequestParam(required = false) String category) {
        return groupService.getGroups(category);
    }

}
