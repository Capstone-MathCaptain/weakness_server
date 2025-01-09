package MathCaptain.weakness.Group.domain;

import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.User.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RELATION_BETWEEN_USER_AND_GROUP")
public class RelationBetweenUserAndGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member")
    private Users member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group groupId;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private int personalDailyGoal;

    @Column(nullable = false)
    private int personalWeeklyGoal;

    // 기본값 설정
    @PrePersist
    protected void onPrePersist() {
        if (this.groupRole == null) {
            this.groupRole = GroupRole.MEMBER; // 기본값 설정
        }
        if (this.joinDate == null) {
            this.joinDate = LocalDateTime.now(); // joinDate의 기본값 설정 (필요 시)
        }
    }
}
