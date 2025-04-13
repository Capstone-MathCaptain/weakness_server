package MathCaptain.weakness.domain.User.repository;

import MathCaptain.weakness.domain.User.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserId(Long userId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByRefreshToken(String refreshToke);
    Optional<Users> findByName(String username);
    Optional<Users> findByNameAndPhoneNumber(String name, String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndName(String email, String name);

}
