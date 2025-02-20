package MathCaptain.weakness.Ranking.domain;

import MathCaptain.weakness.Group.domain.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class GroupRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // Group 객체 추가

    private Long totalGroupPoint;

    public GroupRanking(Group group, Long totalGroupPoint) {
        this.group = group;
        this.totalGroupPoint = totalGroupPoint;
    }

    // 그룹 정보 반환 메서드 추가
    public Long getGroupId() {
        return group != null ? group.getId() : null;
    }

    public String getGroupName() {
        return group != null ? group.getName() : "Unknown";
    }
}
