package MathCaptain.weakness.domain.Chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    private Long userId;

    private String message;

    private ChatRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public static ChatRequest of(Long userId, String message) {
        return new ChatRequest(userId, message);
    }
}
