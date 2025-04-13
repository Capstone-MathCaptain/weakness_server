package MathCaptain.weakness.domain.Record.repository.userLog;

import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyDetail, Long> {
}
