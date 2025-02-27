package MathCaptain.weakness.Group.repository;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findById(Long groupId);

    Optional<Group> findByName(String groupName);

    Boolean existsByName(String groupName);

    Optional<Group> findAllByCategory(CategoryStatus category);

    @Query("SELECT g FROM Group g ORDER BY g.groupPoint DESC")
    Page<Group> findAllOrderByGroupPoint (Pageable pageable);

    Optional<List<Group>> findByNameContaining(String groupName);

}
