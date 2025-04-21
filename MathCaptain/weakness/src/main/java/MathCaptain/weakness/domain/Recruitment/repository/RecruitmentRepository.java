package MathCaptain.weakness.domain.Recruitment.repository;

import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("SELECT r.author.userId FROM Recruitment r WHERE r.id = :recruitmentId")
    Optional<Long> findAuthorIdByRecruitmentId(Long recruitmentId);
}
