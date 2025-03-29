package MathCaptain.weakness.domain.Group.entity;

import MathCaptain.weakness.domain.Group.dto.request.GroupCreateRequest;
import MathCaptain.weakness.domain.Group.dto.request.GroupJoinRequest;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.Group.enums.RequestStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RELATION_BETWEEN_USER_AND_GROUP")
@EntityListeners(AuditingEntityListener.class)
public class RelationBetweenUserAndGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member", referencedColumnName = "user_id")
    private Users member;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "members")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @CreatedDate
    @Column(nullable = false)
    private LocalDate joinDate;

    private RequestStatus requestStatus;

    @Column(nullable = false)
    @Range(min = 0, max = 24)
    private int personalDailyGoal;

    @Range(min = 0)
    @Column(nullable = false)
    private int personalWeeklyGoal;

    // 일간 인증 수행 시간
    @Range(min = 0, max = 1440)
    private Long personalDailyGoalAchieve;

    // 주간 인증 수행 일수
    @Range(min = 0, max = 7)
    private int personalWeeklyGoalAchieve;

    // 주간 목표 달성 연속 횟수
    @Range(min = 0)
    private int weeklyGoalAchieveStreak;

    @Builder
    private RelationBetweenUserAndGroup(Users member, Group group, GroupRole groupRole, int personalDailyGoal, int personalWeeklyGoal) {
        this.member = member;
        this.group = group;
        this.groupRole = groupRole;
        this.requestStatus = RequestStatus.WAITING;
        this.personalDailyGoal = personalDailyGoal;
        this.personalWeeklyGoal = personalWeeklyGoal;
        this.personalDailyGoalAchieve = 0L;
        this.personalWeeklyGoalAchieve = 0;
        this.weeklyGoalAchieveStreak = 0;
    }

    public static RelationBetweenUserAndGroup of(Users member, Group group, int dailyGoal, int weeklyGoal) {
        return RelationBetweenUserAndGroup.builder()
                .member(member)
                .groupRole(GroupRole.MEMBER)
                .group(group)
                .personalDailyGoal(dailyGoal)
                .personalWeeklyGoal(weeklyGoal)
                .build();
    }

    public static RelationBetweenUserAndGroup of(Users leader, Group group, GroupCreateRequest groupCreateRequest) {
        return RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .group(group)
                .personalDailyGoal(groupCreateRequest.getPersonalDailyGoal())
                .personalWeeklyGoal(groupCreateRequest.getPersonalWeeklyGoal())
                .build();
    }

    public static RelationBetweenUserAndGroup of(Users member, Group group, GroupJoinRequest groupJoinRequest) {
        return RelationBetweenUserAndGroup.builder()
                .member(member)
                .groupRole(GroupRole.MEMBER)
                .group(group)
                .personalDailyGoal(groupJoinRequest.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequest.getPersonalWeeklyGoal())
                .build();
    }

    public void subtractPoint(Long point) {
        this.member.subtractPoint(point);
        this.group.subtractPoint(point);
    }

    public boolean isDailyGoalAchieved() {
        return this.personalDailyGoalAchieve >= this.personalDailyGoal;
    }

    public boolean isWeeklyGoalAchieved() {
        return this.personalWeeklyGoalAchieve >= this.personalWeeklyGoal;
    }

    // 일간 목표 업데이트
    public void updatePersonalDailyGoalAchieved(Long personalDailyGoalAchieved) {
        this.personalDailyGoalAchieve = personalDailyGoalAchieved;
    }

    // 주간 목표 업데이트
    public void updatePersonalWeeklyGoalAchieved() {
        this.personalWeeklyGoalAchieve += 1;
    }

    // 주간 목표 달성 연속 횟수 업데이트
    public void updateWeeklyGoalAchieveStreak() {
        this.weeklyGoalAchieveStreak += 1;
    }

    // 일간 목표 초기화
    public void resetPersonalDailyGoalAchieve() {
        this.personalDailyGoalAchieve = 0L;
    }

    // 주간 목표 초기화
    public void resetPersonalWeeklyGoalAchieve() {
        this.personalWeeklyGoalAchieve = 0;
    }

    // 주간 목표 달성 연속 횟수 초기화
    public void resetWeeklyGoalAchieveStreak() {
        this.weeklyGoalAchieveStreak = 0;
    }

    public void updateRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void addPoint(Long point) {
        this.group.addPoint(point);
        this.member.addPoint(point);
    }

    public int weeklyAchieveBase() {
        return this.getWeeklyGoalAchieveStreak() + this.getPersonalWeeklyGoal();
    }

    public void increaseWeeklyGroupCountOf(DayOfWeek dayOfWeek) {
        this.group.increaseWeeklyGoalAchieveMap(dayOfWeek);
    }

    public Long remainingDailyGoalMinutes() {
        return Math.max(this.getPersonalDailyGoal() * 60L - this.getPersonalDailyGoalAchieve(), 0L);
    }

    public int remainingWeeklyGoalDays() {
        return Math.max(this.getPersonalWeeklyGoal() - this.getPersonalWeeklyGoalAchieve(), 0);
    }
}
