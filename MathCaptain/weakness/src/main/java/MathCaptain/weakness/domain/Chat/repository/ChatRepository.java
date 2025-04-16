package MathCaptain.weakness.domain.Chat.repository;

import MathCaptain.weakness.domain.Chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
