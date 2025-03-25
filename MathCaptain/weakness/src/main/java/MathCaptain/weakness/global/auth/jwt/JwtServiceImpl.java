package MathCaptain.weakness.global.auth.jwt;

import MathCaptain.weakness.domain.User.repository.UserRepository;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService{

    //== jwt.yml에 설정된 값 가져오기 ==//
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    //== 1 ==//
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UserRepository usersRepository;

    //== 메서드 ==//

    // AccessToken 생성 (사용자의 email 기반)
    @Override
    public String createAccessToken(String email) {
        return JWT.create()
                // 빌더를 통해 JWT의 Subject 설정 : AccessToken
                .withSubject(ACCESS_TOKEN_SUBJECT)
                // 만료시간 설정
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                // claim으로 email 설정
                .withClaim(USERNAME_CLAIM, email)
                // HMA512 알고리즘을 사용하여, secret키로 암호화
                .sign(Algorithm.HMAC512(secret));
    }

    // RefreshToken 생성
    @Override
    public String createRefreshToken() {
        return JWT.create()
                // 빌더를 통해 JWT의 Subject 설정 : RefreshToken
                .withSubject(REFRESH_TOKEN_SUBJECT)
                // 만료시간 설정
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                // HMA512 알고리즘을 사용하여, secret키로 암호화
                .sign(Algorithm.HMAC512(secret));
    }

    // RefreshToken 유저에게 Update
    @Override
    public void updateRefreshToken(String email, String refreshToken) {
        // 해당 email을 가지는 유저 찾기
        usersRepository.findByEmail(email)
                .ifPresentOrElse(
                        // 존재하면 refreshToken 업데이트
                        users -> users.updateRefreshToken(refreshToken),
                        () -> new ResourceNotFoundException("회원 조회 실패")
                );
    }

    // RefreshToken 삭제
    @Override
    public void destroyRefreshToken(String email) {
        // 해당 email을 가지는 유저 찾기
        usersRepository.findByEmail(email)
                .ifPresentOrElse(
                        // 존재하면 refreshToken 업데이트
                        users -> users.destroyRefreshToken(),
                        () -> new Exception("회원 조회 실패")
                );
    }

    // AccessToken과 RefreshToken을 클라이언트에게 전달
    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info("============= AccessToken과 RefreshToken 전송 =============");
        log.info("🔑AccessToken: {}", accessToken);
        log.info("🗝️RefreshToken: {}", refreshToken);

        // 응답 메시지의 상태를 200 OK로 설정
        response.setStatus(HttpServletResponse.SC_OK);

        // AccessToken, RefreshToken을 헤더에 담음
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        // tokenMap에 두 개의 토큰 내용을 저장
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);

    }

    // AccessToken을 클라리언트에게 전달
    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        log.info("============= AccessToken 전송 =============");
        log.info("🔑AccessToken: {}", accessToken);

        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);

        response.getWriter().write(accessToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);

        log.info("AccessToken 재발급 완료");
    }

    // 클라이언트에게서 전달받은 AccessToken을 HTTP 헤더에서 추출
    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(accessHeader);

        log.info("============= 🔑AccessToken 추출 =============");

        if (headerValue == null || headerValue.isBlank()) {
            log.warn("Authorization 헤더가 비어있거나 존재하지 않습니다.");
            return Optional.empty();
        }

        // "Bearer " 접두사가 있는 경우 제거
        if (headerValue.startsWith(BEARER)) {
            log.info("Authorization 헤더에 'Bearer ' 접두사가 존재합니다. 원본 값: {}", headerValue);
            return Optional.of(headerValue.replace(BEARER, "").trim());
        }

        // "Bearer " 접두사가 없는 경우도 허용 (로그 경고 출력)
        log.warn("Authorization 헤더에 'Bearer ' 접두사가 없습니다. 원본 값: {}", headerValue);
        return Optional.of(headerValue.trim());
    }

    // 클라이언트에게서 전달받은 RefreshToken을 HTTP 헤더에서 추출
    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        log.info("============= 🗝️RefreshToken 추출 =============");
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(
                // BEARER 접두사로 시작하는 RefreshToken을 확인
                refreshToken -> refreshToken.startsWith(BEARER)
                // 찾으면 BEARER 부분을 제외한 RefreshToken을 반환
        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    // AccessToken에서 email을 claim을 추출하여 맞는지 확인
    @Override
    public Optional<String> extractEmail(String accessToken) {
        try {
            Optional<String> email = Optional.ofNullable(
                    // JWT 라이브러리를 이용해 accessToken 속 email을 추출 (USERNAME_CLAIM)
                    JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM)
                            .asString());

            log.info("✉️ email: {}", email);
            return email;
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    // 해당 토큰이 유효한지 검사
    @Override
    public boolean isTokenValid(String token) {
        try {
            log.info("============= 토큰 유효성 검사를 시작합니다. ================");
            log.info("검증 중인 토큰: {}", token);
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("🍪유효하지 않은 Token입니다", e.getMessage());
            return false;
        }
    }

}

