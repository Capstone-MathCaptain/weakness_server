package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.GroupJoin;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupMemberListResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.Group.enums.RequestStatus;
import MathCaptain.weakness.Group.repository.GroupJoinRepository;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupJoinRepository groupJoinRepository;
    private final JwtService jwtService;



    // 그룹 탈퇴
    public ApiResponse<?> leaveGroup(String accessToken, Long groupId) {

        String userEmail = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        Users member = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = getRelation(member, group);

        if (relation.getGroupRole().equals(GroupRole.LEADER)) {
            throw new IllegalArgumentException("리더는 그룹을 탈퇴할 수 없습니다.");
        }

        relationRepository.delete(relation);

        return ApiResponse.ok("그룹 탈퇴가 완료되었습니다.");
    }

    // TODO
    // 그룹장 넘겨주기 기능 ?

    public void leaderJoin(Long groupId, String leaderEmail, GroupJoinRequestDto groupJoinRequestDto) {

        Users leader = userRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        saveRelation(leader, group, groupJoinRequestDto);
    }

    public void saveRelation(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        relationRepository.save(RelationBetweenUserAndGroup.builder()
                .member(member)
                .groupRole(GroupRole.MEMBER)
                .joinGroup(group)
                .personalDailyGoal(groupJoinRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequestDto.getPersonalWeeklyGoal())
                .build());
    }

    // 그룹 멤버 리스트 (그룹 상세 페이지)
    public List<GroupMemberListResponseDto> getGroupMemberList(Long groupId) {

        List<RelationBetweenUserAndGroup> relations = relationRepository.findAllByJoinGroup_id(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹에 멤버가 없습니다."));

        return relations.stream()
                .map(relation -> GroupMemberListResponseDto.builder()
                        .userId(relation.getMember().getUserId())
                        .userName(relation.getMember().getName())
//                        .userImage(relation.getMember().getImage())
                        .userRole(relation.getGroupRole())
                        .userPoint(relation.getMember().getUserPoint())
                        .userWeeklyGoal(relation.getPersonalWeeklyGoal())
                        .isAchieveWeeklyGoal(relation.getIsWeeklyGoalAchieved())
                        .build())
                .collect(Collectors.toList());
    }

    ///  검증 로직

    public RelationBetweenUserAndGroup getRelation(Users member, Group group) {
        return relationRepository.findByMemberAndJoinGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }


    public RelationResponseDto getRelationInfo(Long relationId) {
        RelationBetweenUserAndGroup relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 없습니다."));

        GroupResponseDto group = GroupResponseDto.builder()
                .groupId(relation.getJoinGroup().getId())
                .groupName(relation.getJoinGroup().getName())
                .category(relation.getJoinGroup().getCategory())
                .min_daily_hours(relation.getJoinGroup().getMin_daily_hours())
                .min_weekly_days(relation.getJoinGroup().getMin_weekly_days())
                .group_point(relation.getJoinGroup().getGroup_point())
                .hashtags(relation.getJoinGroup().getHashtags())
                .disturb_mode(relation.getJoinGroup().getDisturb_mode())
                .group_image_url(relation.getJoinGroup().getGroup_image_url())
                .build();

        UserResponseDto member = UserResponseDto.builder()
                .userId(relation.getMember().getUserId())
                .email(relation.getMember().getEmail())
                .name(relation.getMember().getName())
                .nickname(relation.getMember().getNickname())
                .phoneNumber(relation.getMember().getPhoneNumber())
                .build();

        return RelationResponseDto.builder()
                .id(relation.getId())
                .member(member)
                .groupRole(relation.getGroupRole())
                .group(group)
                .joinDate(relation.getJoinDate())
                .personalDailyGoal(relation.getPersonalDailyGoal())
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }

    /// 빌드

    private RelationBetweenUserAndGroup buildLeaderRelation(Users leader, Group group, int dailyGoal, int weeklyGoal) {
        return RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .joinGroup(group)
                .personalDailyGoal(dailyGoal)
                .personalWeeklyGoal(weeklyGoal)
                .build();
    }


}
