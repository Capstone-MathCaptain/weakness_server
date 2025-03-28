package MathCaptain.weakness.domain.User.Login;

import MathCaptain.weakness.domain.User.entity.UserDetailsImpl;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.annotation.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.stereotype.Component;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginUser 어노테이션이 붙어 있고, 파라미터 타입이 Users인 경우 지원
        return parameter.hasParameterAnnotation(LoginUser.class) &&
                parameter.getParameterType().equals(Users.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        // SecurityContext에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 인증되지 않은 경우 null 반환
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUsers(); // Users 객체 반환
        }

        return null; // 인증 정보가 없거나 형식이 다른 경우 null 반환
    }
}