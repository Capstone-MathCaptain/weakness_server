package MathCaptain.weakness.Ranking.repository;

import MathCaptain.weakness.Ranking.domain.GroupRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface GroupRankingRepository extends JpaRepository<GroupRanking, Long> {

    @Query("SELECT gr FROM GroupRanking gr ORDER BY gr.totalGroupPoint DESC")
    List<GroupRanking> findTopRankingGroups();

}