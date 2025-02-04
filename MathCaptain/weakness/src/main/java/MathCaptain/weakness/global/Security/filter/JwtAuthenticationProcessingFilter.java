package MathCaptain.weakness.global.Security.filter;

import MathCaptain.weakness.User.domain.UserDetailsImpl;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final String NO_CHECK_URL = "/login";

     /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
     *
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return; //안해주면 아래로 내려가서 계속 필터를 진행하게됨
        }

        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null); // RefreshToken이 없거나 유효하지 않으면 null을 반환

        if(refreshToken != null){
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken); // refreshToken으로 유저 정보를 찾아오고, 존재하면 AccessToken을 재발급
            return; // 인증을 처리하지 않게 하기 위해 return
        }

        checkAccessTokenAndAuthentication(request, response, filterChain); // refreshToken이 없다면 AccessToken을 검사하는 로직 수행
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(
                // AccessToken이 존재하고 유효하다면
                // AccessToken에서 email을 추출하고, email로 유저 정보를 찾아서 인증처리
                accessToken -> jwtService.extractEmail(accessToken).ifPresent(

                        email -> userRepository.findByEmail(email).ifPresent(

                                users -> saveAuthentication(users)
                        )
                )
        );

        filterChain.doFilter(request,response);
    }

    private void saveAuthentication(Users users) {
        UserDetailsImpl userDetails = new UserDetailsImpl(users);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        // 해당하는 refreshToken이 DB에 존재하면, user에게 AccessToken 발급
        userRepository.findByRefreshToken(refreshToken).ifPresent(
                users -> jwtService.sendAccessToken(response, jwtService.createAccessToken(users.getEmail()))
        );


    }
}
