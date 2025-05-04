package MathCaptain.weakness.domain.Chat.dto.request;

import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.User.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LLMRequest {

    private Long userId;

    private String message;

    private List<Chat> history;

    private LLMRequest(Long userId, String message, List<Chat> history) {
        this.userId = userId;
        this.message = message;
        this.history = history;
    }

    public static LLMRequest of(ChatRequest request, List<Chat> history) {
        return new LLMRequest(request.getUserId(), request.getMessage(), history);
    }
}
