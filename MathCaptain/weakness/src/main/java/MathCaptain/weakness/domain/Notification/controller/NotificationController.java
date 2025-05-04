package MathCaptain.weakness.domain.Notification.controller;

import MathCaptain.weakness.domain.Notification.service.NotificationService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping("/notification/subscribe")
    public SseEmitter subscribe(@LoginUser Users loginUser) {
        return notificationService.subscribe(loginUser);
    }

    @DeleteMapping("/notification/delete/{notificationId}")
    public ApiResponse<?> deleteNotification(@LoginUser Users loginUser, @PathVariable Long notificationId) {
        return notificationService.deleteNotification(loginUser, notificationId);
    }

    @GetMapping("/notification/list")
    public ApiResponse<?> getNotificationList(@LoginUser Users loginUser) {
        return notificationService.getNotificationList(loginUser);
    }
}