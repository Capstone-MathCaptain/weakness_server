//package MathCaptain.weakness.Record.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RealTimeService {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public void sendElapsedTime(Long userId, Long activityId, long elapsedSeconds) {
//        // "/topic/activity/{userId}" 토픽으로 경과 시간 전송
//        messagingTemplate.convertAndSend("/topic/activity/" + userId, elapsedSeconds);
//    }
//
////    public void sendActivitySummary(Long userId, ActivityRecord activityRecord) {
////        // 인증 종료 후 요약 정보를 전송
////        ActivitySummary summary = new ActivitySummary(
////                activityRecord.getDurationInMinutes(),
////                activityRecord.isDailyGoalAchieved()
////        );
////        messagingTemplate.convertAndSend("/topic/activity/summary/" + userId, summary);
////    }
//}