package MathCaptain.weakness.Record.repository;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Record.domain.ActivityRecord;
import MathCaptain.weakness.User.domain.Users;
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

    @Query("SELECT ar.dayOfWeek FROM ActivityRecord ar " +
            "WHERE ar.user = :user AND ar.group = :group " +
            "AND ar.startTime >= :startOfWeek AND ar.startTime < :endOfWeek")
    List<DayOfWeek> findDaysWithActivity(
            @Param("user") Users user,
            @Param("group") Group group,
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);
}
