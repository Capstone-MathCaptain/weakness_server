package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final JwtService jwtService;

    // 그룹 참여
    public ApiResponse<?> joinGroup(Long groupId, String accessToken, GroupJoinRequestDto groupJoinRequestDto, HttpServletResponse response) {

        Users joinUser = jwtService.extractEmail(accessToken)
                .map(userService::getUserByEmail)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        // 이미 가입한 경우 & 목표 조건 달성 여부
        checkJoin(joinUser, group, groupJoinRequestDto.getPersonalDailyGoal(), groupJoinRequestDto.getPersonalWeeklyGoal());

        saveRelation(joinUser, group, groupJoinRequestDto);

        String newAccessToken = jwtService.createAccessToken(joinUser.getEmail());

        response.setHeader("Authorization", newAccessToken);

        return ApiResponse.ok("그룹 가입이 완료되었습니다.");
    }

    // 그룹 탈퇴
    public ApiResponse<?> leaveGroup(String accessToken, Long groupId, HttpServletResponse response) {
        String userEmail = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        Users member = userService.getUserByEmail(userEmail);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = getRelation(member, group);
        relationRepository.delete(relation);

        String newAccessToken = jwtService.createAccessToken(member.getEmail());

        response.setHeader("Authorization", newAccessToken);

        return ApiResponse.ok("그룹 탈퇴가 완료되었습니다.");
    }

    public void leaderJoin(Long groupId, String leaderEmail, GroupJoinRequestDto groupJoinRequestDto) {
        Users leader = userService.getUserByEmail(leaderEmail);
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

    //==검증 로직==/
    private void checkJoin(Users member, Group joinGroup, int personalDailyGoal, int personalWeeklyGoal) {

        checkRelation(member, joinGroup);

        if (personalDailyGoal < joinGroup.getMin_daily_hours()) {
            throw new IllegalArgumentException("하루 목표 시간은 " + joinGroup.getMin_daily_hours() + "시간 이상이어야 합니다.");
        }

        if (personalWeeklyGoal < joinGroup.getMin_weekly_days()) {
            throw new IllegalArgumentException("주간 목표 일수는 " + joinGroup.getMin_weekly_days() + "일 이상이어야 합니다.");
        }
    }

    public void checkRelation(Users member, Group group) {
        if (relationRepository.findByMemberAndJoinGroup(member, group).isPresent()) {
            throw new IllegalArgumentException("해당 멤버가 이미 가입되어 있습니다.");
        }
    }

    public RelationBetweenUserAndGroup getRelation(Users member, Group group) {
        return relationRepository.findByMemberAndJoinGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }


    public RelationResponseDto getRelationInfo(Long relationId) {
        RelationBetweenUserAndGroup relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 없습니다."));

        GroupResponseDto group = getGroupResponseDto(relation.getJoinGroup());

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

    static GroupResponseDto getGroupResponseDto(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .leaderId(group.getLeader().getUserId())
                .leaderName(group.getLeader().getName())
                .groupName(group.getName())
                .category(group.getCategory())
                .min_daily_hours(group.getMin_daily_hours())
                .min_weekly_days(group.getMin_weekly_days())
                .group_point(group.getGroup_point())
                .hashtags(group.getHashtags())
                .disturb_mode(group.getDisturb_mode())
                .created_date(group.getCreate_date())
                .group_image_url(group.getGroup_image_url())
                .build();
    }


}
