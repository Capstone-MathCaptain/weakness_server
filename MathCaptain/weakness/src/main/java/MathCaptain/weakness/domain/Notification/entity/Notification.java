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

    @Column(nullable = false)
    private String sender;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String contents;

    @Builder
    private Notification(String sender, LocalDateTime createdAt, String contents) {
        this.sender = sender;
        this.createdAt = createdAt;
        this.contents = contents;
    }

    public static Notification of(Map<String, String> eventData) {
        return Notification.builder()
                .sender(eventData.get("sender"))
                .createdAt(LocalDateTime.parse(eventData.get("createdAt")))
                .contents(eventData.get("message"))
                .build();
    }
}
