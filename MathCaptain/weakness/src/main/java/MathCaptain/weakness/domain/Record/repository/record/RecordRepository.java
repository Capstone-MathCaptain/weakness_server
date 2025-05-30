package MathCaptain.weakness.domain.Record.repository.record;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.User.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<ActivityRecord, Long> {

    Optional<ActivityRecord> findByUserAndGroupAndEndTimeIsNull(Users user, Group group);

    @Query("SELECT ar.dayOfWeek FROM activity_record ar " +
            "WHERE ar.user = :user AND ar.group = :group " +
            "AND ar.startTime >= :startOfWeek AND ar.startTime < :endOfWeek")
    List<DayOfWeek> findDaysWithActivity(
            @Param("user") Users user,
            @Param("group") Group group,
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);

    // 이번주에 그룹원이 수행한 인증의 횟수
    @Query("SELECT SUM (CASE WHEN ar.dailyGoalAchieved = true THEN 1 ELSE 0 END) " +
            "FROM activity_record ar " +
            "WHERE ar.group.id = :groupId " +
            "AND ar.user.userId = :userId " +
            "AND ar.startTime >= :startOfWeek AND ar.startTime < :endOfWeek")
    Optional<Integer> countDailyGoalAchieved(@Param("groupId") Long groupId,
                                   @Param("userId") Long userId,
                                   @Param("startOfWeek") LocalDateTime startOfWeek,
                                   @Param("endOfWeek") LocalDateTime endOfWeek);
}
