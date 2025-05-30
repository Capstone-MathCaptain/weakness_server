package MathCaptain.weakness.global.config;

import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Group.service.GroupService;
import MathCaptain.weakness.domain.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.domain.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.global.auth.filter.GroupRoleFilter;
import MathCaptain.weakness.global.auth.filter.JsonUsernamePasswordAuthenticationFilter;
import MathCaptain.weakness.global.auth.filter.JwtAuthenticationProcessingFilter;
import MathCaptain.weakness.global.auth.jwt.JwtService;
import MathCaptain.weakness.domain.User.Login.LoginSuccessJWTProvideHandler;
import MathCaptain.weakness.domain.User.Login.LoginFailureHandler;
import MathCaptain.weakness.domain.User.service.UserDetailsServiceImpl;
import MathCaptain.weakness.domain.User.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final GroupService groupService;
    private final UserRepository userRepository;
    private final RelationRepository relationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;


	@Bean
	public WebSecurityCustomizer configure() {
		return (web -> web.ignoring()
                .requestMatchers(PathRequest
                        .toStaticResources()
                        .atCommonLocations())
                .requestMatchers("/static/**")
                .requestMatchers("/templates/**")
                .requestMatchers("/error")
		);
	}

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http	.csrf(AbstractHttpConfigurer::disable)  // REST API 사용으로 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form Login 비활성화
                .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class) // 추가 : 커스터마이징 된 필터를 SpringSecurityFilterChain에 등록
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/static/**", "/templates/**").permitAll() // 정적 리소스 접근 허용
                        .requestMatchers("/user/signup", "/login", "/user/find/email","/user/reset/password", "/user/find/password").permitAll() // 접근 허용된 URL
                        .anyRequest().authenticated()) // 나머지 URL은 인증된 사용자만 접근 가능
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)) // 로그아웃 이후 전체 세션 삭제 여부
                .sessionManagement(session -> session // 세션 생성 여부 및 사용 여부에 대한 정책 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http
                .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class) // 커스터마이징 된 필터를 추가
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(groupRoleFilter(), JwtAuthenticationProcessingFilter.class);
        return http.build();
    }

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    // 인증 관리자 관련 설정
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    // 비밀번호 암호화
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // AuthenticationManager 등록 및 사용
    @Bean
    public AuthenticationManager authenticationManager() throws Exception { //AuthenticationManager 등록
        DaoAuthenticationProvider provider = daoAuthenticationProvider(); //DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder()); //PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        return new ProviderManager(provider);
    }

    // JWT를 통한 로그인 성공 Handler 등록
    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService, groupService, relationRepository, userRepository, objectMapper);
    }

    // JWT 통한 로그인 실패 Handler 등록
    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    // JWT 토큰을 추출하여 인증 객체(Authentication)를 생성
    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
    }

    @Bean
    public GroupRoleFilter groupRoleFilter(){
        return new GroupRoleFilter(relationRepository, userRepository, recruitmentRepository, commentRepository);
    }

}