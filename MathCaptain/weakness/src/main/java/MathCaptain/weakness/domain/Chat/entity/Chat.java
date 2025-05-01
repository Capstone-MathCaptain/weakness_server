package MathCaptain.weakness.domain.Chat.entity;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.common.enums.ChatRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ChatRole role;

    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sendTime;

    @Builder
    private Chat(Long chatRoomId, Long userId, ChatRole role, String message) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.role = role;
        this.message = message;
    }

    public static Chat of(ChatRequest request, ChatRole role) {
        return new Chat(request.getChatRoomId(), request.getUserId(), role, request.getMessage());
    }

    public static Chat of(ChatResponse response) {
        return Chat.builder()
                .chatRoomId(response.getChatRoomId())
                .role(ChatRole.ASSISTANT)
                .message(response.getMessage())
                .build();
    }
}
