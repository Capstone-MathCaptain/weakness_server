package MathCaptain.weakness.Notification.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Notification.controller.NotificationController;
import MathCaptain.weakness.Notification.domain.Notification;
import MathCaptain.weakness.Notification.repository.NotificationRepository;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;
    private final GroupRepository groupRepository;
    private final NotificationRepository notificationRepository;
    private final RelationRepository relationRepository;

    private static Map<Long, Integer> notificationCounts = new HashMap<>();


    // 메시지 알림
    public SseEmitter subscribe(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationController.sseEmitters.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(userId));

        return sseEmitter;
    }

    public void notifyComment(Long recruitmentId, Long commentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 존재하지 않습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        Long userId = recruitment.getAuthor().getUserId();

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try {
                Map<String, String> eventData = buildNotificationData("댓글이 달렸습니다.", comment.getAuthor().getNickname(), comment.getCommentTime().toString(), comment.getContent());

                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                notificationRepository.save(buildNotification(eventData));

                // 알림 개수 증가
                notificationCounts.put(userId, notificationCounts.getOrDefault(userId, 0) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));

            } catch (IOException e) {
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }

    public void notifyGroupJoinRequest(Long groupId, Users user) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        Users leader = relationRepository.findLeaderByGroup(group)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹의 리더가 존재하지 않습니다."));

        Long leaderId = leader.getUserId();

        if (NotificationController.sseEmitters.containsKey(leaderId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(leaderId);
            try {
                Map<String,String> eventData = buildNotificationData("그룹 가입 요청이 있습니다.", user.getNickname(), LocalDateTime.now().toString(), "가입 요청");

                sseEmitter.send(SseEmitter.event().name("groupJoinRequest").data(eventData));

                // DB 저장
                notificationRepository.save(buildNotification(eventData));

                // 알림 개수 증가
                updateNotificationCount(leaderId);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(leaderId)));

            } catch (IOException e) {
                NotificationController.sseEmitters.remove(leaderId);
            }
        }

    }

    public void notifyGroupJoinResult(Long groupId, Users user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroup(user, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        Long userId = user.getUserId();

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try {
                Map<String,String> eventData = buildNotificationData("그룹 가입 요청 결과가 도착했습니다.", group.getName(), LocalDateTime.now().toString(), relation.getRequestStatus().toString());

                sseEmitter.send(SseEmitter.event().name("groupJoinResult").data(eventData));

                // DB 저장
                notificationRepository.save(buildNotification(eventData));

                // 알림 개수 증가
                updateNotificationCount(userId);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));

            } catch (IOException e) {
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }

    public ApiResponse<?> deleteNotification(Users loginUser, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        Long userId = loginUser.getUserId();

        notificationRepository.delete(notification);

        // 알림 개수 감소
        if (notificationCounts.containsKey(userId)) {
            int currentCount = notificationCounts.get(userId);
            if (currentCount > 0) {
                notificationCounts.put(userId, currentCount - 1);
            }
        }

        // 현재 알림 개수 전송
        SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
        try {
            sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ApiResponse.ok("알림이 삭제되었습니다.");
    }

    private Map<String, String> buildNotificationData(String message, String sender, String createdAt, String contents) {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("message", message);
        eventData.put("sender", sender);
        eventData.put("createdAt", createdAt);
        eventData.put("contents", contents);
        return eventData;
    }

    private void updateNotificationCount(Long userId) {
        notificationCounts.put(userId, notificationCounts.getOrDefault(userId, 0) + 1);
    }

    private Notification buildNotification(Map<String, String> eventData) {
        return Notification.builder()
                .sender(eventData.get("sender"))
                .createdAt(LocalDateTime.parse(eventData.get("createdAt")))
                .contents(eventData.get("message"))
                .build();
    }



    // 알림 삭제
//        public MsgResponseDto deleteNotification(Long id) throws IOException {
//            Notification notification = notificationRepository.findById(id).orElseThrow(
//                    () -> new IllegalArgumentException("알림을 찾을 수 없습니다.")
//            );
//
//            Long userId = notification.getPost().getUser().getId();
//
//            notificationRepository.delete(notification);
//
//            // 알림 개수 감소
//            if (notificationCounts.containsKey(userId)) {
//                int currentCount = notificationCounts.get(userId);
//                if (currentCount > 0) {
//                    notificationCounts.put(userId, currentCount - 1);
//                }
//            }
//
//            // 현재 알림 개수 전송
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
//            sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));
//
//            return new MsgResponseDto("알림이 삭제되었습니다.", HttpStatus.OK.value());
//        }

}
