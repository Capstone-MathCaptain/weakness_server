package MathCaptain.weakness.Group.repository;

import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<RelationBetweenUserAndGroup, Long> {
}
