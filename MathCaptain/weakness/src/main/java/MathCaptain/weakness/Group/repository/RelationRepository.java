package MathCaptain.weakness.Group.repository;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.User.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<RelationBetweenUserAndGroup, Long> {

    @Query("SELECT r.member FROM RelationBetweenUserAndGroup r WHERE r.joinGroup = :group")
    List<Users> findMembersByGroup(@Param("group") Group group);

    Optional<RelationBetweenUserAndGroup> findByMemberAndJoinGroup(Users member, Group group);

    @Query("SELECT r FROM RelationBetweenUserAndGroup r WHERE r.member.userId = :userId and r.joinGroup.id = :groupId")
    Optional<RelationBetweenUserAndGroup> findByMemberIdAndJoinGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("SELECT r.joinGroup.id FROM RelationBetweenUserAndGroup r WHERE r.member.userId = :userId")
    List<Long> findGroupsIdByUserId(@Param("userId") Long userId);

    @Query("SELECT r.joinGroup.id FROM RelationBetweenUserAndGroup r WHERE r.member.email = :email")
    List<Long> findGroupsIdByEmail(@Param("email") String email);

    @Query("SELECT r.joinGroup.id FROM RelationBetweenUserAndGroup r WHERE r.member = :user")
    List<Long> findGroupsIdByMember(@Param("user") Users user);

    Optional<List<RelationBetweenUserAndGroup>> findAllByMember_Email(String email);

    Optional<RelationBetweenUserAndGroup> findByMember_EmailAndJoinGroup_Id(String memberEmail, Long joinGroupId);

    Optional<RelationBetweenUserAndGroup> findByMemberAndJoinGroup_Id(Users member, Long joinGroupId);

    Optional<RelationBetweenUserAndGroup> findByMemberAndGroupRole(Users member, GroupRole groupRole);

    Optional<List<RelationBetweenUserAndGroup>> findAllByJoinGroup_id(Long joinGroupId);

    boolean existsByMember_EmailAndGroupRole(String memberEmail, GroupRole groupRole);

    Long countByJoinGroup_Id(Long joinGroupId);

    // 같은 그룹에 속하는 사람들 모두가 주간 목표 달성을 했는지 여부
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END " +
            "FROM RelationBetweenUserAndGroup r " +
            "WHERE r.joinGroup.id = :groupId AND r.isWeeklyGoalAchieved = false")
    boolean allMembersAchievedWeeklyGoal(@Param("groupId") Long groupId);
}

