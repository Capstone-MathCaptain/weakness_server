package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "GROUPS")
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "joinGroup")
    @Column(name = "members")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryStatus category;

    // 하루 최소 수행 시간
    private int min_daily_hours;

    private int min_weekly_days;

    private Long group_point;

    @ElementCollection
    @CollectionTable(name = "group_hashtags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "hashtag")
    private List<String> hashtags;

    private Boolean disturb_mode;

    private LocalDate create_date;

    private String group_image_url;

    @OneToMany(mappedBy = "recruitGroup")
    private List<Recruitment> recruitments;

    @PrePersist
    protected void onCreate() {
        this.create_date = LocalDate.now();
    }

    //==수정 로직==//

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCategory(CategoryStatus category) {
        this.category = category;
    }

    public void updateMinDailyHours(int min_daily_hours) {
        this.min_daily_hours = min_daily_hours;
    }

    public void updateMinWeeklyDays(int min_weekly_days) {
        this.min_weekly_days = min_weekly_days;
    }

    public void updateGroupPoint(Long group_point) {
        this.group_point = group_point;
    }

    public void updateHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void updateDisturbMode(Boolean disturb_mode) {
        this.disturb_mode = disturb_mode;
    }

    public void updateGroupImageUrl(String group_image_url) {
        this.group_image_url = group_image_url;
    }
}
