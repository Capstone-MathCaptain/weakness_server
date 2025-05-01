package MathCaptain.weakness.domain.Chat.service;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.Chat.repository.ChatRepository;
import MathCaptain.weakness.domain.common.enums.ChatRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    public static final ChatRole USER = ChatRole.USER;
    private final ChatRepository chatRepository;
    private final LLMClient llm;

    @Transactional
    public ChatResponse saveUserMessage(ChatRequest request) {
        Chat message = Chat.of(request, USER);
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }

    public List<ChatResponse> askAI(ChatRequest request) {
        List<Chat> history = chatRepository.findAllByChatRoomIdOrderBySendTimeAsc(request.getChatRoomId());
        List<Chat> aiChats = llm.call(history, request);
        return aiChats.stream()
                      .map(this::storeAndTransform)
                      .toList();
    }

    private ChatResponse storeAndTransform(Chat message) {
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }
}
