package MathCaptain.weakness.domain.Notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String sender;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String content;

    private Boolean isRead;

    @Builder
    private Notification(Long userId, String sender, LocalDateTime createdAt, String content) {
        this.userId = userId;
        this.sender = sender;
        this.createdAt = createdAt;
        this.content = content;
        this.isRead = false;
    }

    public static Notification of(Map<String, String> eventData) {
        return Notification.builder()
                .userId(Long.parseLong(eventData.get("userId")))
                .sender(eventData.get("sender"))
                .content(eventData.get("message"))
                .build();
    }
}
