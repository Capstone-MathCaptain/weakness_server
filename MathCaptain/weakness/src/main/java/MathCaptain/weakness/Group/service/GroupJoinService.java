package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupJoinResponseDto;
import MathCaptain.weakness.Group.enums.RequestStatus;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
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

    public static final RequestStatus WAITING = RequestStatus.WAITING;

    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;

    // 그룹 참여
    public void joinGroupRequest(Long groupId, Users user, GroupJoinRequestDto groupJoinRequestDto) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        // 이미 가입한 경우 & 목표 조건 달성 여부
        checkJoin(user, group, groupJoinRequestDto);

        // 그룹 가입 요청 저장
        saveJoinRequest(user, group, groupJoinRequestDto);
    }

    // 그룹 가입 상태 변경
    public void changeStatus(Long groupId, Users user, RequestStatus status) {
        RelationBetweenUserAndGroup relation = checkStatusIsWaiting(groupId, user);
        relation.updateRequestStatus(status);
    }

    // 해당 그룹에 전달된 가입 요청 리스트
    public ApiResponse<List<GroupJoinResponseDto>> getJoinRequestList(Long groupId) {
        List<RelationBetweenUserAndGroup> requestList = relationRepository.findByGroupAndRequestStatus(groupId, WAITING);

        return ApiResponse.ok(requestList.stream()
                .map(this::buildGroupJoinResponseDto)
                .toList());
    }

    //==검증 로직==//
    private void checkJoin(Users member, Group joinGroup, GroupJoinRequestDto groupJoinRequestDto) {

        if (relationRepository.findByMemberAndGroup(member, joinGroup).isPresent()) {
            throw new IllegalArgumentException("해당 멤버가 이미 가입되어 있습니다.");
        }

        int personalDailyGoal = groupJoinRequestDto.getPersonalDailyGoal();
        int personalWeeklyGoal = groupJoinRequestDto.getPersonalWeeklyGoal();

        if (checkJoinAble(joinGroup, personalDailyGoal, personalWeeklyGoal)) {
            throw new IllegalArgumentException("목표 조건을 달성하지 못했습니다.");
        }
    }


    private RelationBetweenUserAndGroup checkStatusIsWaiting(Long groupId, Users user) {
        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroup_Id(user, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        if (!relation.getRequestStatus().equals(WAITING)) {
            throw new IllegalArgumentException("가입 요청 상태가 아닙니다.");
        }

        return relation;
    }


    private boolean checkJoinAble(Group joinGroup, int personalDailyGoal, int personalWeeklyGoal) {
        return !joinGroup.checkJoin(personalDailyGoal, personalWeeklyGoal);
    }

    //==빌드==//
    public void saveJoinRequest(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        RelationBetweenUserAndGroup relation = RelationBetweenUserAndGroup.builder()
                .member(member)
                .group(group)
                .personalDailyGoal(groupJoinRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequestDto.getPersonalWeeklyGoal())
                .requestStatus(WAITING)
                .build();

        relationRepository.save(relation);
    }

    private GroupJoinResponseDto buildGroupJoinResponseDto(RelationBetweenUserAndGroup relation) {
        Users user = relation.getMember();

        return GroupJoinResponseDto.builder()
                .groupJoinId(relation.getGroup().getId())
                .userId(user.getUserId())
                .userNickname(user.getNickname())
                .userPoint(user.getUserPoint())
                .personalDailyGoal(relation.getPersonalDailyGoal())
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }
}
