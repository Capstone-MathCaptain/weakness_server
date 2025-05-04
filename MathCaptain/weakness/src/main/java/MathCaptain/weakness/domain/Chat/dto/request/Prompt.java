package MathCaptain.weakness.domain.Chat.dto.request;

import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class Prompt {

    private String template;

    private Map<String, String> inputs;

    private List<ChatResponse> history;

    private Prompt(Long userId, String message, List<ChatResponse> history) {
        this.template = "질문의 내용에 답변을 할 때 사용자의 활동기록과 문서에서 검색된 내용이 필요하면 참고해서 답변해줘";
        this.inputs = Map.of(
                "user_id", userId.toString(),
                "user_question", message
        );
        this.history = history;
    }

    public static Prompt of(ChatRequest request, List<ChatResponse> history) {
        return new Prompt(request.getUserId(), request.getMessage(), history);
    }
}
