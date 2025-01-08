package MathCaptain.weakness.Group;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.time.Duration;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "user_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long leader_id;

    @NotNull
    private String name;

    @OneToOne
    @NotNull
    private Category category;

    // 하루 최소 수행 시간
    @Range(min = 1, max = 24)
    private Duration min_daily_hours;

    @Range(min = 1)
    private int min_weekly_days;

    private Long group_point;

    private String hashtag;

    @Column(nullable = false)
    private boolean disturb_mode;

    private LocalDate create_date;

    private String group_image_url;
}
