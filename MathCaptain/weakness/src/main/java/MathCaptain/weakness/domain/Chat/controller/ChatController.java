package MathCaptain.weakness.domain.Chat.controller;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.service.ChatService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    private final ChatService chatService;

    @GetMapping("/chat/history/{userId}")
    public List<ChatResponse> getHistory(@PathVariable Long userId) {
        return chatService.getHistory(userId);
    }

    @MessageMapping("/chat.send")  // 클라이언트 → /app/chat.send
    public void handleChat(ChatRequest request) {
        // 1) 유저 메시지 저장 + 클라이언트에게 에코
        ChatResponse chatResponse = chatService.saveUserMessage(request);
        template.convertAndSend("/sub/" + request.getUserId(), chatResponse);

        // 2) LLM 호출 & 응답 저장 + 브로드캐스트 (동기 방식)
        List<ChatResponse> llmResponses = chatService.askAI(request);
        llmResponses.forEach(aiResp ->
            template.convertAndSend("/sub/" + request.getUserId(), aiResp)
        );
    }
}
