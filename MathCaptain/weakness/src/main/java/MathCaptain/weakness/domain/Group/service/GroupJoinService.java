package MathCaptain.weakness.domain.Group.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.dto.request.GroupJoinRequest;
import MathCaptain.weakness.domain.Group.dto.response.GroupJoinResponse;
import MathCaptain.weakness.domain.common.enums.RequestStatus;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.User.entity.Users;
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
    public void joinGroupRequest(Long groupId, Users user, GroupJoinRequest groupJoinRequest) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
        // 이미 가입한 경우 & 목표 조건 달성 여부
        isJoined(user, group, groupJoinRequest);
        RelationBetweenUserAndGroup relation = RelationBetweenUserAndGroup.of(user, group, groupJoinRequest);
        relationRepository.save(relation);
    }

    // 그룹 가입 상태 변경
    public void changeStatus(Long groupId, Users user, RequestStatus status) {
        RelationBetweenUserAndGroup relation = checkStatusIsWaiting(groupId, user);
        relation.updateRequestStatus(status);
    }

    // 해당 그룹에 전달된 가입 요청 리스트
    public ApiResponse<List<GroupJoinResponse>> getJoinRequestList(Long groupId) {
        List<RelationBetweenUserAndGroup> requestList = relationRepository.findByGroupAndRequestStatus(groupId, WAITING);

        return ApiResponse.ok(requestList.stream()
                .map(GroupJoinResponse::of)
                .toList());
    }

    //==검증 로직==//
    private void isJoined(Users member, Group joinGroup, GroupJoinRequest groupJoinRequest) {
        if (relationRepository.findByMemberAndGroup(member, joinGroup).isPresent()) {
            throw new IllegalArgumentException("해당 멤버가 이미 가입되어 있습니다.");
        }
        if (joinAble(joinGroup, groupJoinRequest)) {
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

    private boolean joinAble(Group joinGroup, GroupJoinRequest groupJoinRequest) {
        return !joinGroup.checkJoin(groupJoinRequest.getPersonalDailyGoal(), groupJoinRequest.getPersonalWeeklyGoal());
    }
}
