package MathCaptain.weakness.Recruitment.repository;

import MathCaptain.weakness.Recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("SELECT r.author.userId FROM Recruitment r WHERE r.postId = :recruitmentId")
    Optional<Long> findAuthorIdByRecruitmentId(Long recruitmentId);
}
