package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.CategoryStatus;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupId")
    @Column(name = "members")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoryStatus category;

    // 하루 최소 수행 시간
    @Range(min = 1, max = 24)
    private int min_daily_hours;

    @Range(min = 1)
    private int min_weekly_days;

    @Range(min = 0)
    private Long group_point;

    @ElementCollection
    @CollectionTable(name = "group_hashtags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "hashtag")
    private List<String> hashtags;

    @Column(nullable = false)
    private Boolean disturb_mode;

    private LocalDate create_date;

    private String group_image_url;

    @PrePersist
    protected void onCreate() {
        this.create_date = LocalDate.now();
    }
}
