package MathCaptain.weakness.domain.Chat.dto.request;

import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class LLMRequest {

    private Prompt prompt;

    private Map<String, Object> resources;

    private List<Object> tools;

    private LLMRequest(Prompt prompt) {
        this.prompt = prompt;
        this.resources = new HashMap<>();
        this.tools = List.of();
    }

    public static LLMRequest of(ChatRequest request, List<ChatResponse> history) {
        Prompt prompt = Prompt.of(request, history);
        return new LLMRequest(prompt);
    }
}
