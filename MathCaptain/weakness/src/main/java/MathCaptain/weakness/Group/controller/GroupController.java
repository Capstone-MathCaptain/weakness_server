package MathCaptain.weakness.Group.controller;

import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupSearchRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupDetailResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupMemberListResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.Group.service.GroupJoinService;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.service.RelationService;
import MathCaptain.weakness.global.Api.ApiResponse;
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
    private final GroupJoinService groupJoinService;

    /// 그룹 CRUD

    // 유저가 속한 그룹을 모두 보여줌
    @GetMapping("/group")
    public ApiResponse<List<GroupResponseDto>> getUsersGroups(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return ApiResponse.ok(groupService.getUsersGroups(accessToken));
    }

    // 그룹 조회
    @GetMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> groupInfo(@PathVariable Long groupId) {
        GroupResponseDto groupResponseDto = groupService.getGroupInfo(groupId);
        return ApiResponse.ok(groupResponseDto);
    }

    // 그룹 생성
    @PostMapping("/group")
    public ApiResponse<GroupResponseDto> createGroup(@Valid @RequestHeader("Authorization") String authorizationHeader, @RequestBody GroupCreateRequestDto groupCreateRequestDto, HttpServletResponse response) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return groupService.createGroup(groupCreateRequestDto, accessToken, response);
    }

    // 그룹 정보 수정
    @PutMapping("/group/{groupId}")
    public ApiResponse<GroupResponseDto> updateGroup(@Valid @PathVariable Long groupId, @RequestBody GroupUpdateRequestDto groupUpdateRequestDto) {
        return groupService.updateGroupInfo(groupId, groupUpdateRequestDto);
    }

    // 그룹 가입 요청 보내기
    @PostMapping("/group/join/{groupId}")
    public ApiResponse<?> joinGroup(@Valid @PathVariable Long groupId,
                                    @RequestHeader("Authorization") String authorizationHeader,
                                    @RequestBody GroupJoinRequestDto groupJoinRequestDto) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return groupJoinService.joinGroupRequest(groupId, accessToken, groupJoinRequestDto);
    }

    // 그룹 삭제
    @DeleteMapping("/group/{groupId}")
    public ApiResponse<?> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }

    ///  그룹 가입
    // 그룹 가입 요청 수락
    @PostMapping("/group/join/accept/{groupId}/{joinRequestId}")
    public ApiResponse<?> acceptJoinRequest(@PathVariable Long groupId, @PathVariable Long joinRequestId) {
        return groupJoinService.acceptJoinRequest(groupId, joinRequestId);
    }

    // 그룹 가입 요청 거절
    @PostMapping("/group/join/reject/{groupId}/{joinRequestId}")
    public ApiResponse<?> rejectJoinRequest(@PathVariable Long joinRequestId) {
        return groupJoinService.rejectJoinRequest(joinRequestId);
    }

    // 그룹 가입 요청 취소
    @DeleteMapping("/group/join/cancel/{joinRequestId}")
    public ApiResponse<?> cancelJoinRequest(@PathVariable Long joinRequestId) {
        return groupJoinService.cancelJoinRequest(joinRequestId);
    }

    // 그룹 가입 요청 조회
    @GetMapping("/group/join/{groupId}")
    public ApiResponse<?> getJoinRequest(@PathVariable Long groupId) {
        return groupJoinService.getJoinRequestList(groupId);
    }

    // 그룹 떠나기 (탈퇴)
    @DeleteMapping("/group/leave/{groupId}")
    public ApiResponse<?> leaveGroup(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long groupId) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return relationService.leaveGroup(accessToken, groupId);
    }

    /// 조회
    // 그룹 멤버 조회
    @GetMapping("/group/members/{groupId}")
    public ApiResponse<List<UserResponseDto>> groupMembers(@PathVariable Long groupId) {
        List<UserResponseDto> members = groupService.getGroupMembers(groupId);
        return ApiResponse.ok(members);
    }

    // 그룹 관계 조회
    @GetMapping("/group/relation/{relationId}")
    public RelationResponseDto relationInfo(@PathVariable Long relationId) {
        return relationService.getRelationInfo(relationId);
    }


    // 그룹 상세 정보
    @GetMapping("/group/detail/{groupId}")
    public ApiResponse<GroupDetailResponseDto> groupDetail(@PathVariable Long groupId) {
        return ApiResponse.ok(groupService.getGroupDetail(groupId));
    }

    // 그룹 멤버 리스트 (그룹 상세 페이지)
    @GetMapping("/group/detail/{groupId}/members")
    public ApiResponse<List<GroupMemberListResponseDto>> GroupMemberList(@PathVariable Long groupId) {
        return ApiResponse.ok(relationService.getGroupMemberList(groupId));
    }

    // 그룹 검색 (그룹명)
    @PostMapping("/group/search")
    public ApiResponse<GroupResponseDto> searchGroup(@RequestBody GroupSearchRequestDto groupSearchRequestDto) {
        return ApiResponse.ok(groupService.getGroupInfo(groupSearchRequestDto.getGroupName()));
    }
}
