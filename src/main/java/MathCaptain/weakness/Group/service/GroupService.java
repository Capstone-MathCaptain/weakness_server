package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.GroupCreateDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;

    public Long createGroup(long userId, GroupCreateDto groupCreateDto) {

        Users leader = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Group group = Group.builder()
                .leader(leader)
                .name(groupCreateDto.getGroup_name())
                .category(groupCreateDto.getCategory())
                .min_daily_hours(groupCreateDto.getMin_daily_hours())
                .min_weekly_days(groupCreateDto.getMin_weekly_days())
                .group_point(groupCreateDto.getGroup_point())
                .hashtags(groupCreateDto.getHashtags())
                .disturb_mode(groupCreateDto.getDisturb_mode())
                .group_image_url(groupCreateDto.getGroup_image_url())
                .build();

        Long groupId = groupRepository.save(group).getId();

        Group leader_group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다."));

        RelationBetweenUserAndGroup leaderAndCreateGroup = RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .groupId(leader_group)
                .personalDailyGoal(groupCreateDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupCreateDto.getPersonalWeeklyGoal())
                .build();
        relationRepository.save(leaderAndCreateGroup);

        return groupId;
    }

    public Group getGroupInfo(long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다."));
    }
}
