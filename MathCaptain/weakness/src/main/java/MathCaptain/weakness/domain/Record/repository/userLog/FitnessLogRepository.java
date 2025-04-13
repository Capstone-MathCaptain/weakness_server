package MathCaptain.weakness.domain.Record.repository.userLog;

import MathCaptain.weakness.domain.Record.entity.UserLog.FitnessDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitnessLogRepository extends JpaRepository<FitnessDetail, Long> {
}
