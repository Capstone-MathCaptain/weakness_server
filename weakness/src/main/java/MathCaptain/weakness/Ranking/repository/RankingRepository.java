package MathCaptain.weakness.Ranking.repository;

import MathCaptain.weakness.Ranking.domain.PersonalRanking;
import MathCaptain.weakness.Ranking.domain.PersonalRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RankingRepository extends JpaRepository<PersonalRanking, Long> {

    @Query("SELECT r FROM Ranking r ORDER BY r.personalPoint DESC")
    List<PersonalRanking> findTopRankingUsers();
}
