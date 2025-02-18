package MathCaptain.weakness.global.Security.filter;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.service.RelationService;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 URI 확인
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        // PUT 요청만 처리
        // 그룹 정보 수정 요청인 경우 (그룹 리더만 가능)
        if (httpMethod.equalsIgnoreCase("PUT") && requestURI.matches("^/group/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[2]);

            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();


            if (isGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 그룹 가입 요청 리스트 조회는 리더만 가능
        if (httpMethod.equalsIgnoreCase("GET") && requestURI.matches("^/group/join/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[3]);
            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            if (isGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 그룹 가입 요청 승인/거절은 리더만 가능
        if (httpMethod.equalsIgnoreCase("POST") && requestURI.matches("^/group/join/[a-zA-Z]+/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[4]);
            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            if (isGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private boolean isGroupLeader(String userEmail, Long groupId) {
        Users member = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndJoinGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 해당 그룹에 가입하지 않았습니다."));

        return relation.getGroupRole() == GroupRole.LEADER;
    }

    private void denyAccess(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(message);
    }
}
