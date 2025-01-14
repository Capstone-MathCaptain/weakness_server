package MathCaptain.weakness.Recruitment.repository;

import MathCaptain.weakness.Recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
}
