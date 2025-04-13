package MathCaptain.weakness.domain.Record.repository.userLog;

import MathCaptain.weakness.domain.Record.entity.UserLog.RunningDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningLogRepository extends JpaRepository<RunningDetail, Long> {
}
