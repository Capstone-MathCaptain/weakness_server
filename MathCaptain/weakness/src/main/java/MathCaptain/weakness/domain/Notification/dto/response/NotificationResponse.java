package MathCaptain.weakness.domain.Notification.dto.response;

import MathCaptain.weakness.domain.Notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String sender;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isRead;

    @Builder
    public NotificationResponse(Long id, String sender, String content, LocalDateTime createdAt, Boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .sender(notification.getSender())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .build();
    }

    public static List<NotificationResponse> fromList(List<Notification> notifications) {
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
}