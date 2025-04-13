package MathCaptain.weakness.domain.Group.entity;

import MathCaptain.weakness.domain.Group.dto.request.GroupCreateRequest;
import MathCaptain.weakness.domain.Group.dto.request.GroupUpdateRequest;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Table(name = "UserGroup")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.REMOVE)
    @Column(name = "members")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryStatus category;

    // 하루 최소 수행 시간
    @Column(nullable = false)
    private int minDailyHours;

    @Column(nullable = false)
    private int minWeeklyDays;

    private Long groupPoint;

    private int groupRanking;

    @ElementCollection
    @CollectionTable(name = "group_hashtags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "hashtag")
    private List<String> hashtags;

    @CreatedDate
    private LocalDate createDate;

    private String groupImageUrl;

    // 요일별 목표 인증을 완료한 멤버들의 수를 저장하는 Map
    @ElementCollection
    @CollectionTable(name = "group_weekly_goal_achieve", joinColumns = @JoinColumn(name = "group_id"))
    @MapKeyColumn(name = "day_of_week") // 요일을 키로 사용
    @Column(name = "goal_count") // 카운트를 값으로 사용
    private final Map<DayOfWeek, Integer> weeklyGoalAchieveMap = new EnumMap<>(DayOfWeek.class);

    @OneToMany(mappedBy = "recruitGroup")
    private List<Recruitment> recruitments;

    @Builder
    private Group(List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup, String name, CategoryStatus category, int minDailyHours, int minWeeklyDays, List<String> hashtags, String groupImageUrl) {
        this.relationBetweenUserAndGroup = relationBetweenUserAndGroup;
        this.name = name;
        this.category = category;
        this.minDailyHours = minDailyHours;
        this.minWeeklyDays = minWeeklyDays;
        this.createDate = LocalDate.now();
        this.groupPoint = 0L;
        this.hashtags = hashtags;
        this.groupImageUrl = groupImageUrl;
    }

    public static Group of(GroupCreateRequest groupCreateRequest) {
        return Group.builder()
                .name(groupCreateRequest.getGroupName())
                .category(groupCreateRequest.getCategory())
                .minDailyHours(groupCreateRequest.getMinDailyHours())
                .minWeeklyDays(groupCreateRequest.getMinWeeklyDays())
                .hashtags(groupCreateRequest.getHashtags())
                .groupImageUrl(groupCreateRequest.getGroupImageUrl())
                .build();
    }

    @PrePersist
    protected void onCreate() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            weeklyGoalAchieveMap.put(dayOfWeek, 0);
        }
    }

    //==수정 로직==//
    public void updateGroupPoint(Long group_point) {
        this.groupPoint = group_point;
    }

    public void updateGroupRanking(int groupRanking) {
        this.groupRanking = groupRanking;
    }

    public void updateWeeklyGoalAchieveMap(DayOfWeek dayOfWeek, int goalCount) {
        weeklyGoalAchieveMap.put(dayOfWeek, goalCount);
    }

    public void updateGroup(GroupUpdateRequest requestDto) {
        this.name = requestDto.getGroupName();
        this.minDailyHours = requestDto.getMinDailyHours();
        this.minWeeklyDays = requestDto.getMinWeeklyDays();
        this.hashtags = requestDto.getHashtags();
        this.groupImageUrl = requestDto.getGroupImageUrl();
    }

    public boolean checkJoin(int dailyGoal, int weeklyGoal) {
        return dailyGoal >= minDailyHours && weeklyGoal >= minWeeklyDays;
    }

    public void increaseWeeklyGoalAchieveMap(DayOfWeek day) {
        weeklyGoalAchieveMap.put(day, weeklyGoalAchieveMap.get(day) + 1);
    }

    public void resetWeeklyGoalAchieveMap() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            weeklyGoalAchieveMap.put(dayOfWeek, 0);
        }
    }

    public void addPoint(Long point) {
        this.groupPoint += point;
    }

    public void subtractPoint(Long point) {
        this.groupPoint = Math.max(0, this.groupPoint - point);
    }
}
