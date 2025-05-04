package MathCaptain.weakness.domain.Chat.service;

import MathCaptain.weakness.domain.Chat.dto.request.ChatRequest;
import MathCaptain.weakness.domain.Chat.dto.request.LLMRequest;
import MathCaptain.weakness.domain.Chat.dto.response.ChatResponse;
import MathCaptain.weakness.domain.Chat.entity.Chat;
import MathCaptain.weakness.domain.User.entity.Users;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Component
@RequiredArgsConstructor
public class LLMClient {

    private final RestTemplate restTemplate;

    @Value("${llm.server.url}")
    private String baseUrl;

    public Chat call(List<ChatResponse> history, ChatRequest request) {
        try {
            LLMRequest llmRequest = LLMRequest.of(request, history);

            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                    // TODO : 엔드포인트 수정
                    baseUrl + "/mcp/query",
                    llmRequest,
                    // 응답을 ChatResponse[] (배열)로 역직렬화 (수정 필요)
                    ChatResponse.class
            );

            ChatResponse body = response.getBody();
            if (body == null) {
                throw new HttpServerErrorException(HttpStatus.NOT_ACCEPTABLE, "LLM이 응답하지 않습니다");
            }
            return Chat.of(body);

        } catch (ResourceAccessException timeoutEx) {
            log.error("LLM 서버 응답 지연", timeoutEx);
            throw new ResourceAccessException("LLM 서버 응답 지연");
        } catch (HttpClientErrorException | HttpServerErrorException httpEx) {
            // 4XX/5XX 응답
            log.error("LLM 호출 실패: {}", httpEx.getStatusCode(), httpEx);
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, "LLM 호출 실패");
        }
    }
}