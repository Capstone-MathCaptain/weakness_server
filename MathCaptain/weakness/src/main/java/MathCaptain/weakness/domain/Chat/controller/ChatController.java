package MathCaptain.weakness.domain.Chat.controller;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.service.ChatService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    private final ChatService chatService;

    @MessageMapping("/chat.send")  // 클라이언트 → /app/chat.send
    public void handleChat(ChatRequest request, @LoginUser Users loginUser) {
        // 1) 유저 메시지 저장 + 클라이언트에게 에코
        ChatResponse chatResponse = chatService.saveUserMessage(loginUser, request);
        template.convertAndSend("/sub/" + loginUser.getUserId(), chatResponse);

        // 2) LLM 호출 & 응답 저장 + 브로드캐스트 (동기 방식)
        List<ChatResponse> llmResponses = chatService.askAI(loginUser, request);
        llmResponses.forEach(aiResp ->
            template.convertAndSend("/sub/" + loginUser.getUserId(), aiResp)
        );
    }
}
