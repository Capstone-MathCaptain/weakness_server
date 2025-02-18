package MathCaptain.weakness.Login;

import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import MathCaptain.weakness.User.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final GroupService groupService;
    private final RelationRepository relationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        userRepository.findByEmail(email).ifPresent(
                user -> user.updateRefreshToken(refreshToken)
        );

        List<Long> groupsId = relationRepository.findGroupsIdByEmail(email);

        List<GroupResponseDto> groupResponseDtoList = groupService.getUsersGroups(groupsId);

        // groupResponseDtoList를 JSON 형태로 변환
        ApiResponse<List<GroupResponseDto>> apiResponse = ApiResponse.ok(groupResponseDtoList);

        // ApiResponse를 JSON 형식으로 변환
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        // JSON 응답 설정 및 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // JSON 형태로 변환된 데이터를 응답
        response.getWriter().write(jsonResponse);

        log.info( "로그인에 성공합니다. email: {}" , email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,refreshToken);
        log.info( "사용자의 그룹 정보를 조회합니다. 사용자가 속한 그룹들의 ID: {}" ,groupsId);

    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
