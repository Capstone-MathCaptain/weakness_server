package MathCaptain.weakness.domain.Chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {

    private Long chatRoomId;

    private Long userId;

    private String message;
}
