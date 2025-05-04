package MathCaptain.weakness.domain.Chat.service;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.request.LLMRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.Chat.repository.ChatRepository;
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
        Chat message = Chat.of(request);
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }

    public List<ChatResponse> askAI(ChatRequest request) {
        List<ChatResponse> history = chatRepository.findAllByUserIdOrderBySendTimeAsc(request.getUserId()).stream()
                .map(ChatResponse::of)
                .toList();

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

    @Transactional
    public LLMRequest test(Long userId, ChatRequest request) {
        List<ChatResponse> history = chatRepository.findAllByUserIdOrderBySendTimeAsc(userId).stream()
                .map(ChatResponse::of)
                .toList();

        return LLMRequest.of(request, history);
    }

    private ChatResponse storeAndTransform(Chat message) {
        Chat saved = chatRepository.save(message);
        return ChatResponse.of(saved);
    }
}
