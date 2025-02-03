package MathCaptain.weakness.Record.repository;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Record.domain.ActivityRecord;
import MathCaptain.weakness.User.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<ActivityRecord, Long> {

    Optional<ActivityRecord> findByUserAndGroupAndEndTimeIsNull(Users user, Group group);
}
