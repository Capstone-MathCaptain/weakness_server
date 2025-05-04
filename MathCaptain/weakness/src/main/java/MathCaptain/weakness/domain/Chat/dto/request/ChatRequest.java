package MathCaptain.weakness.domain.Chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    private String message;

    private ChatRequest(String message) {
        this.message = message;
    }

    public static ChatRequest of(String message) {
        return new ChatRequest(message);
    }
}
