package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Builder
@Table(name = "UserGroup")
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader")
    private Users leader;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "joinGroup", cascade = CascadeType.REMOVE)
    @Column(name = "members")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryStatus category;

    // 하루 최소 수행 시간
    private int minDailyHours;

    private int minWeeklyDays;

    private Long groupPoint;

    private int groupRanking;

    @ElementCollection
    @CollectionTable(name = "group_hashtags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "hashtag")
    private List<String> hashtags;

    private LocalDate createDate;

    private String groupImageUrl;

    // 요일별 목표 인증을 완료한 멤버들의 수를 저장하는 Map
    @ElementCollection
    @CollectionTable(name = "group_weekly_goal_achieve", joinColumns = @JoinColumn(name = "group_id"))
    @MapKeyColumn(name = "day_of_week") // 요일을 키로 사용
    @Column(name = "goal_count") // 카운트를 값으로 사용
    private Map<DayOfWeek, Integer> weeklyGoalAchieveMap;

    @OneToMany(mappedBy = "recruitGroup")
    private List<Recruitment> recruitments;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();

        if (this.weeklyGoalAchieveMap == null) {
            this.weeklyGoalAchieveMap = new EnumMap<>(DayOfWeek.class);
        }
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            weeklyGoalAchieveMap.put(dayOfWeek, 0);
        }
    }

    //==수정 로직==//

    public void updateName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
        }
    }

    public void updateCategory(CategoryStatus category) {
        if (category != null && !category.equals(this.category)) {
            this.category = category;
        }
    }

    public void updateMinDailyHours(int min_daily_hours) {
        if (min_daily_hours > 0 &&  min_daily_hours != this.minDailyHours) {
            this.minDailyHours = min_daily_hours;
        }
    }

    public void updateMinWeeklyDays(int min_weekly_days) {
        if (min_weekly_days > 0 && min_weekly_days != this.minWeeklyDays) {
            this.minWeeklyDays = min_weekly_days;
        }
    }

    public void updateGroupPoint(Long group_point) {
        this.groupPoint = group_point;
    }

    public void updateHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void updateGroupImageUrl(String group_image_url) {
        if (group_image_url != null && !group_image_url.equals(this.groupImageUrl)) {
            this.groupImageUrl = group_image_url;
        }
    }

    public void updateGroupRanking(int groupRanking) {
        this.groupRanking = groupRanking;
    }

    public void updateWeeklyGoalAchieveMap(DayOfWeek dayOfWeek, int goalCount) {
        weeklyGoalAchieveMap.put(dayOfWeek, goalCount);
    }

    public void updateGroup(GroupUpdateRequestDto requestDto) {
        updateName(requestDto.getGroupName());
        updateMinDailyHours(requestDto.getMinDailyHours());
        updateMinWeeklyDays(requestDto.getMinWeeklyDays());
        updateHashtags(requestDto.getHashtags());
        updateGroupImageUrl(requestDto.getGroupImageUrl());
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
