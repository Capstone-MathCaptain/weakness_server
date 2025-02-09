package MathCaptain.weakness.Group.repository;

import MathCaptain.weakness.Group.domain.GroupJoin;
import MathCaptain.weakness.Group.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupJoinRepository extends JpaRepository<GroupJoin, Long> {

    Optional<List<GroupJoin>> findAllByGroup_idAndRequestStatus(Long groupId, RequestStatus requestStatus);
}
