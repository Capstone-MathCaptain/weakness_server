package MathCaptain.weakness.domain.Chat.dto.request;

import MathCaptain.weakness.domain.Chat.entity.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LLMRequest {

    private Long chatRoomId;

    private Long userId;

    private String message;

    private List<Chat> history;

    private LLMRequest(Long chatRoomId, Long userId, String message, List<Chat> history) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.message = message;
        this.history = history;
    }

    public static LLMRequest of(ChatRequest request, List<Chat> history) {
        return new LLMRequest(request.getChatRoomId(), request.getUserId(), request.getMessage(), history);
    }
}
