package MathCaptain.weakness.domain.Chat.service;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.Chat.repository.ChatRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.common.enums.ChatRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final LLMClient llm;

    @Transactional
    public ChatResponse saveUserMessage(ChatRequest request) {
        log.info("userid {}", request.getUserId());
        log.info("message {}", request.getMessage());
        Chat message = Chat.of(request);
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }

    public List<ChatResponse> askAI(ChatRequest request) {
        List<Chat> history = chatRepository.findAllByUserIdOrderBySendTimeAsc(request.getUserId());
        List<Chat> aiChats = llm.call(history, request);
        return aiChats.stream()
                      .map(this::storeAndTransform)
                      .toList();
    }

    @Transactional
    public List<ChatResponse> getHistory(Long userId) {
        List<Chat> history = chatRepository.findAllByUserIdOrderBySendTimeAsc(userId);
        return history.stream()
                      .map(ChatResponse::of)
                      .toList();
    }

    private ChatResponse storeAndTransform(Chat message) {
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }
}
