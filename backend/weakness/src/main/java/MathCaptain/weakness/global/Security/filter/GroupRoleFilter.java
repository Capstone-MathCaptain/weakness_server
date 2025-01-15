package MathCaptain.weakness.global.Security.filter;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.service.RelationService;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class GroupRoleFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final GroupService groupService;
    private final RelationService relationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 URI 확인
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        // PUT 요청만 처리
        if (httpMethod.equalsIgnoreCase("PUT") && requestURI.matches("^/group/\\d+$")) {
            // groupId 추출
            Long groupId = Long.parseLong(requestURI.split("/")[2]);

            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // RelationBetweenUserAndGroup에서 역할 확인
            Users member = userService.getUserByName(username);

            Group group = groupService.getGroup(groupId);

            RelationBetweenUserAndGroup relation = relationService.getRelation(member, group);

            if (relation == null || relation.getGroupRole() != GroupRole.LEADER) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: 그룹 리더만 수정할 수 있습니다!");
                return; // 필터 체인 중단
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }


}
