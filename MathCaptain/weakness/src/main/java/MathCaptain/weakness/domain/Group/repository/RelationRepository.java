package MathCaptain.weakness.domain.Group.repository;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.common.enums.GroupRole;
import MathCaptain.weakness.domain.common.enums.RequestStatus;
import MathCaptain.weakness.domain.User.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<RelationBetweenUserAndGroup, Long> {

    @Query("SELECT r.member FROM RelationBetweenUserAndGroup r WHERE r.group = :group")
    List<Users> findMembersByGroup(@Param("group") Group group);

    Optional<RelationBetweenUserAndGroup> findByMemberAndGroup(Users member, Group group);

    @Query("SELECT r FROM RelationBetweenUserAndGroup r WHERE r.member.userId = :userId and r.group.id = :groupId")
    Optional<RelationBetweenUserAndGroup> findByMemberIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);

    @Query("SELECT r.group.id FROM RelationBetweenUserAndGroup r WHERE r.member.userId = :userId")
    List<Long> findGroupsIdByUserId(@Param("userId") Long userId);

    @Query("SELECT r.group FROM RelationBetweenUserAndGroup r WHERE r.member = :user")
    List<Group> findGroupsByMember(@Param("user") Users user);

    @Query("SELECT SUM(r.personalWeeklyGoal) FROM RelationBetweenUserAndGroup r WHERE r.group.id = :groupId")
    Integer sumPersonalWeeklyGoalByGroupId(@Param("groupId") Long groupId);

    Optional<RelationBetweenUserAndGroup> findByMember_EmailAndGroup_Id(String memberEmail, Long joinGroupId);

    Optional<RelationBetweenUserAndGroup> findByMemberAndGroup_Id(Users member, Long joinGroupId);

    Optional<RelationBetweenUserAndGroup> findByMemberAndGroupRole(Users member, GroupRole groupRole);

    Optional<List<RelationBetweenUserAndGroup>> findAllByGroup_id(Long joinGroupId);

    @Query("SELECT r FROM RelationBetweenUserAndGroup r WHERE r.group.id = :groupId AND r.requestStatus = :requestStatus")
    List<RelationBetweenUserAndGroup> findByGroupAndRequestStatus(Long groupId, RequestStatus requestStatus);

    boolean existsByMember_EmailAndGroupRole(String memberEmail, GroupRole groupRole);

    @Query("SELECT r.member FROM RelationBetweenUserAndGroup r WHERE r.group = :group AND r.groupRole = 'LEADER'")
    Optional<Users> findLeaderByGroup(Group group);

    Long countByGroup(Group joinGroup);

    // 같은 그룹에 속하는 사람들 모두가 주간 목표 달성을 했는지 여부
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END " +
            "FROM RelationBetweenUserAndGroup r " +
            "WHERE r.group.id = :groupId AND r.personalDailyGoalAchieve >= r.personalDailyGoal")
    boolean allMembersAchievedWeeklyGoal(@Param("groupId") Long groupId);
}

