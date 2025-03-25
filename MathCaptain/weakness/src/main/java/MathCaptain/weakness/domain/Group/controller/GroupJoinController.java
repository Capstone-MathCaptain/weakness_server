package MathCaptain.weakness.domain.Group.controller;

import MathCaptain.weakness.domain.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.domain.Group.enums.RequestStatus;
import MathCaptain.weakness.domain.Group.service.GroupJoinService;
import MathCaptain.weakness.domain.Group.service.RelationService;
import MathCaptain.weakness.domain.Notification.service.NotificationService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupJoinController {

    public static final RequestStatus ACCEPTED = RequestStatus.ACCEPTED;
    public static final RequestStatus REJECTED = RequestStatus.REJECTED;
    public static final RequestStatus CANCELED = RequestStatus.CANCELED;

    private final RelationService relationService;
    private final GroupJoinService groupJoinService;
    private final NotificationService notificationService;

    ///  그룹 가입

    // 그룹 가입 요청 보내기
    @PostMapping("/group/join/{groupId}")
    public ApiResponse<?> joinGroup(@Valid @PathVariable Long groupId,
                                    @LoginUser Users loginUser,
                                    @RequestBody GroupJoinRequestDto groupJoinRequestDto) {
        groupJoinService.joinGroupRequest(groupId, loginUser, groupJoinRequestDto);
        notificationService.notifyGroupJoinRequest(groupId, loginUser);
        return ApiResponse.ok("그룹 가입 요청이 완료되었습니다.");
    }

    // 그룹 가입 요청 수락
    @PostMapping("/group/join/accept/{groupId}")
    public ApiResponse<?> acceptJoinRequest(@PathVariable Long groupId, @LoginUser Users loginUser) {
        groupJoinService.changeStatus(groupId, loginUser, ACCEPTED);
        notificationService.notifyGroupJoinResult(groupId, loginUser);
        return ApiResponse.ok("그룹 가입 요청이 수락되었습니다.");
    }

    // 그룹 가입 요청 거절
    @PostMapping("/group/join/reject/{groupId}")
    public ApiResponse<?> rejectJoinRequest(@PathVariable Long groupId, @LoginUser Users loginUser) {
        groupJoinService.changeStatus(groupId, loginUser, REJECTED);
        notificationService.notifyGroupJoinResult(groupId, loginUser);
        return ApiResponse.ok("그룹 가입 요청이 거절되었습니다.");
    }

    // 그룹 가입 요청 취소
    @DeleteMapping("/group/join/cancel/{groupId}")
    public ApiResponse<?> cancelJoinRequest(@PathVariable Long groupId, @LoginUser Users loginUser) {
        groupJoinService.changeStatus(groupId, loginUser, CANCELED);
        return ApiResponse.ok("그룹 가입 요청이 취소되었습니다.");
    }

    // 그룹 가입 요청 조회
    @GetMapping("/group/join/{groupId}")
    public ApiResponse<?> getJoinRequest(@PathVariable Long groupId) {
        return groupJoinService.getJoinRequestList(groupId);
    }

    // 그룹 떠나기 (탈퇴)
    @DeleteMapping("/group/leave/{groupId}")
    public ApiResponse<?> leaveGroup(@LoginUser Users loginUser, @PathVariable Long groupId) {
        return relationService.leaveGroup(loginUser, groupId);
    }

}
