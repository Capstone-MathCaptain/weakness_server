package MathCaptain.weakness.global.Web;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 경로
//        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 경로
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws") // WebSocket 연결 엔드포인트
//                .setAllowedOrigins("*") // 모든 도메인 허용 (CORS 설정)
//                .withSockJS(); // SockJS를 활성화하여 WebSocket 미지원 환경에서도 동작 가능
//    }
//}
