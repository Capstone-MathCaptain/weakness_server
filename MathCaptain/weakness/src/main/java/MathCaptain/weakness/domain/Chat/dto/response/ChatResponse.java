package MathCaptain.weakness.domain.Chat.dto.response;

import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.common.enums.ChatRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatResponse {

    private Long userId;

    private ChatRole role;

    private String message;

    private LocalDateTime sendTime;

    private ChatResponse(Long userId, ChatRole role, String message, LocalDateTime sendTime) {
        this.userId = userId;
        this.role = role;
        this.message = message;
        this.sendTime = sendTime;
    }

    public static ChatResponse of(Chat chat) {
        return new ChatResponse(
                chat.getUserId(),
                chat.getRole(),
                chat.getMessage(),
                chat.getSendTime()
        );
    }
}
