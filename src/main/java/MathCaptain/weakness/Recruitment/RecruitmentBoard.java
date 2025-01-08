package MathCaptain.weakness.Recruitment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
public class RecruitmentBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private Long userId;

    private Integer categoryId;

    private Long authorId;

    private String title;

    private String content;

    @Column(nullable = false)
    private Boolean recruitmentStatus;

    private Integer commentCount;

    private Integer interestCount;

    @Column(nullable = false)
    private LocalDateTime postTime;
}
