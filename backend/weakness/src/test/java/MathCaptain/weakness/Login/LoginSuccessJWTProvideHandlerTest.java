package MathCaptain.weakness.Login;

import MathCaptain.weakness.global.Security.jwt.JwtService;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginSuccessJWTProvideHandlerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should issue tokens and update refresh token on successful authentication")
    void shouldIssueTokensAndUpdateRefreshTokenOnSuccessfulAuthentication() throws IOException, ServletException {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtService.createAccessToken(anyString())).thenReturn("accessToken");
        when(jwtService.createRefreshToken()).thenReturn("refreshToken");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Users.class)));

        loginSuccessJWTProvideHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtService).sendAccessAndRefreshToken(response, "accessToken", "refreshToken");
        verify(userRepository).findByEmail("test@example.com");
        verify(response.getWriter()).write("success");
    }

    @Test
    @DisplayName("Should handle missing user in repository")
    void shouldHandleMissingUserInRepository() throws IOException, ServletException {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtService.createAccessToken(anyString())).thenReturn("accessToken");
        when(jwtService.createRefreshToken()).thenReturn("refreshToken");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        loginSuccessJWTProvideHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtService).sendAccessAndRefreshToken(response, "accessToken", "refreshToken");
        verify(userRepository).findByEmail("test@example.com");
        verify(response.getWriter()).write("success");
    }

    @Test
    @DisplayName("Should handle IOException during response writing")
    void shouldHandleIOExceptionDuringResponseWriting() throws IOException, ServletException {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtService.createAccessToken(anyString())).thenReturn("accessToken");
        when(jwtService.createRefreshToken()).thenReturn("refreshToken");
        when(response.getWriter()).thenThrow(new IOException());

        loginSuccessJWTProvideHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtService).sendAccessAndRefreshToken(response, "accessToken", "refreshToken");
        verify(userRepository).findByEmail("test@example.com");
    }
}