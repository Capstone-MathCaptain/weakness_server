package MathCaptain.weakness.Group.repository;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.User.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<RelationBetweenUserAndGroup, Long> {

    @Query("SELECT r.member FROM RelationBetweenUserAndGroup r WHERE r.group = :group")
    List<Users> findMembersByGroup(@Param("group") Group group);

    Optional<RelationBetweenUserAndGroup> findByMemberAndGroup(Users member, Group group);
}
