package MathCaptain.weakness.User.repository;

import MathCaptain.weakness.User.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserId(long userId);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByRefreshToken(String refreshToke);
    Optional<Users> findByName(String username);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);

}
