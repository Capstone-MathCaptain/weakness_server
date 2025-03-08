package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupMemberListResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Record.repository.RecordRepository;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;
    private final RecordRepository recordRepository;

    // 그룹 탈퇴
    public ApiResponse<?> leaveGroup(Users user, Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = getRelation(user, group);

        if (isLeader(relation)) {
            throw new IllegalArgumentException("리더는 그룹을 탈퇴할 수 없습니다.");
        }

        relationRepository.delete(relation);

        return ApiResponse.ok("그룹 탈퇴가 완료되었습니다.");
    }

    public void leaderJoin(Long groupId, Users leader, GroupJoinRequestDto groupJoinRequestDto) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        saveRelation(leader, group, groupJoinRequestDto);
    }

    public void saveRelation(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        relationRepository.save(buildRelation(member, group, groupJoinRequestDto));
    }

    // 그룹 멤버 리스트 (그룹 상세 페이지)
    public List<GroupMemberListResponseDto> getGroupMemberList(Long groupId) {
        // 그룹 멤버 관계 조회
        List<RelationBetweenUserAndGroup> relations = relationRepository.findAllByGroup_id(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹에 멤버가 없습니다."));

        // 이번 주의 시작과 끝 시간 계산
        LocalDateTime startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

        // 멤버 리스트 생성
        return relations.stream()
                .map(relation -> mapToGroupMemberListResponseDto(relation, startOfWeek, endOfWeek))
                .collect(Collectors.toList());
    }
    ///  검증 로직

    public RelationBetweenUserAndGroup getRelation(Users member, Group group) {
        return relationRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }

    private static boolean isLeader(RelationBetweenUserAndGroup relation) {
        return relation.getGroupRole().equals(GroupRole.LEADER);
    }

    public RelationResponseDto getRelationInfo(Long relationId) {
        RelationBetweenUserAndGroup relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 없습니다."));

        GroupResponseDto group = GroupResponseDto.builder()
                .groupId(relation.getGroup().getId())
                .groupName(relation.getGroup().getName())
                .category(relation.getGroup().getCategory())
                .minDailyHours(relation.getGroup().getMinDailyHours())
                .minWeeklyDays(relation.getGroup().getMinWeeklyDays())
                .groupPoint(relation.getGroup().getGroupPoint())
                .hashtags(relation.getGroup().getHashtags())
                .groupImageUrl(relation.getGroup().getGroupImageUrl())
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

    private GroupMemberListResponseDto mapToGroupMemberListResponseDto(RelationBetweenUserAndGroup relation, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        // 기록 리포지토리에서 현재 진행 상황 가져오기 (null 방지)
        Integer currentProgress = recordRepository.countDailyGoalAchieved(
                relation.getGroup().getId(),
                relation.getMember().getUserId(),
                startOfWeek,
                endOfWeek
        ).orElse(0);

        // DTO 빌드 및 반환
        return GroupMemberListResponseDto.builder()
                .userId(relation.getMember().getUserId())
                .userName(relation.getMember().getName())
                //.userImage(relation.getMember().getImage()) // 주석 유지 또는 복원 필요 시 활성화
                .userRole(relation.getGroupRole())
                .userPoint(relation.getMember().getUserPoint())
                .userWeeklyGoal(relation.getPersonalWeeklyGoal())
                .userDailyGoal(relation.getPersonalDailyGoal())
                .isWeeklyGoalAchieved(relation.isWeeklyGoalAchieved())
                .currentProgress(currentProgress)
                .build();
    }


    private RelationBetweenUserAndGroup buildLeaderRelation(Users leader, Group group, int dailyGoal, int weeklyGoal) {
        return RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .group(group)
                .personalDailyGoal(dailyGoal)
                .personalWeeklyGoal(weeklyGoal)
                .build();
    }

    private RelationBetweenUserAndGroup buildRelation(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        return RelationBetweenUserAndGroup.builder()
                .member(member)
                .groupRole(GroupRole.MEMBER)
                .group(group)
                .personalDailyGoal(groupJoinRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequestDto.getPersonalWeeklyGoal())
                .build();
    }


}
