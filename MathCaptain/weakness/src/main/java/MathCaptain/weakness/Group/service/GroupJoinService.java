package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.GroupJoin;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupJoinResponseDto;
import MathCaptain.weakness.Group.enums.RequestStatus;
import MathCaptain.weakness.Group.repository.GroupJoinRepository;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupJoinService {

    private final GroupRepository groupRepository;
    private final GroupJoinRepository groupJoinRepository;
    private final RelationRepository relationRepository;
    private final RelationService relationService;

    // 그룹 참여
    public ApiResponse<?> joinGroupRequest(Long groupId, Users user, GroupJoinRequestDto groupJoinRequestDto) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        // 이미 가입한 경우 & 목표 조건 달성 여부
        checkJoin(user, group, groupJoinRequestDto.getPersonalDailyGoal(), groupJoinRequestDto.getPersonalWeeklyGoal());

        // 그룹 가입 요청 저장
        saveJoinRequest(user, group, groupJoinRequestDto);

        // TODO
        // 해당 그룹장에게 알림 보내기

        return ApiResponse.ok("그룹 가입 신청이 완료되었습니다.");
    }

    // 그룹 가입 요청 수락
    public ApiResponse<?> acceptJoinRequest(Long groupId, Long joinRequestId) {

        GroupJoin joinRequest = groupJoinRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 요청이 존재하지 않습니다."));

        if (joinRequest.getRequestStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("이미 거절된 요청입니다.");
        } else if (joinRequest.getRequestStatus() == RequestStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 요청입니다.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        Users joinUser = joinRequest.getUser();

        // 가입 요청 수락
        relationService.saveRelation(joinUser, group, GroupJoinRequestDto.builder()
                .personalDailyGoal(joinRequest.getPersonalDailyGoal())
                .personalWeeklyGoal(joinRequest.getPersonalWeeklyGoal())
                .build());

        joinRequest.updateRequestStatus(RequestStatus.ACCEPTED);

        // TODO
        // 회원에게 그룹 가입 알림 보내기

        return ApiResponse.ok("가입 요청이 수락되었습니다.");
    }

    // 그룹 가입 요청 거절
    public ApiResponse<?> rejectJoinRequest(Long joinRequestId) {

        GroupJoin joinRequest = groupJoinRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 요청이 존재하지 않습니다."));

        if (joinRequest.getRequestStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("이미 가입된 요청입니다.");
        } else if (joinRequest.getRequestStatus() == RequestStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 요청입니다.");
        }

        joinRequest.updateRequestStatus(RequestStatus.REJECTED);

        // TODO
        // 회원에게 가입 거절 알림 보내기

        return ApiResponse.ok("가입 요청이 거절되었습니다.");
    }

    // 그룹 가입 요청 취소
    public ApiResponse<?> cancelJoinRequest(Long joinRequestId) {
        GroupJoin joinRequest = groupJoinRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가입 요청이 존재하지 않습니다."));

        if (joinRequest.getRequestStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("이미 그룹에 가입되어 취소할 수 없습니다.");
        } else if (joinRequest.getRequestStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("이미 거절된 요청입니다.");
        }

        joinRequest.updateRequestStatus(RequestStatus.CANCELED);

        return ApiResponse.ok("가입 요청이 취소되었습니다.");
    }

    // 해당 그룹에 전달된 가입 요청 리스트
    public ApiResponse<List<GroupJoinResponseDto>> getJoinRequestList(Long groupId) {
        List<GroupJoin> requestList = groupJoinRepository.findAllByGroup_idAndRequestStatus(groupId, RequestStatus.WAITING)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹에 가입 요청이 존재하지 않습니다."));

        return ApiResponse.ok(requestList.stream()
                .map(this::buildGroupJoinResponseDto)
                .toList());
    }

    //==검증 로직==//

    private void checkJoin(Users member, Group joinGroup, int personalDailyGoal, int personalWeeklyGoal) {

        if (relationRepository.findByMemberAndJoinGroup(member, joinGroup).isPresent()) {
            throw new IllegalArgumentException("해당 멤버가 이미 가입되어 있습니다.");
        }

        if (personalDailyGoal < joinGroup.getMinDailyHours()) {
            throw new IllegalArgumentException("하루 목표 시간은 " + joinGroup.getMinDailyHours() + "시간 이상이어야 합니다.");
        }

        if (personalWeeklyGoal < joinGroup.getMinWeeklyDays()) {
            throw new IllegalArgumentException("주간 목표 일수는 " + joinGroup.getMinWeeklyDays() + "일 이상이어야 합니다.");
        }
    }

    //==빌드==//
    public void saveJoinRequest(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        groupJoinRepository.save(GroupJoin.builder()
                .user(member)
                .group(group)
                .personalDailyGoal(groupJoinRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequestDto.getPersonalWeeklyGoal())
                .requestStatus(RequestStatus.WAITING)
                .build());
    }

    private GroupJoinResponseDto buildGroupJoinResponseDto(GroupJoin groupJoin) {
        return GroupJoinResponseDto.builder()
                .groupJoinId(groupJoin.getGroupJoinId())
                .userId(groupJoin.getUser().getUserId())
                .userNickname(groupJoin.getUser().getNickname())
                .userPoint(groupJoin.getUser().getUserPoint())
                .personalDailyGoal(groupJoin.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoin.getPersonalWeeklyGoal())
                .build();
    }
}
