package MathCaptain.weakness.global.Security.filter;

import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Api.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class GroupRoleFilter extends OncePerRequestFilter {

    private final RelationRepository relationRepository;
    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 URI 확인
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        /// 그룹 CRUD 관련 요청
        // PUT 요청만 처리
        // 그룹 정보 수정 요청인 경우 (그룹 리더만 가능)
        if (httpMethod.equalsIgnoreCase("PUT") && requestURI.matches("^/group/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[2]);

            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();


            if (isNotGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 그룹 삭제는 리더만 가능
        if (httpMethod.equalsIgnoreCase("DELETE") && requestURI.matches("^/group/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[2]);
            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            if (isNotGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        /// 그룹 가입 관련 요청
        // 그룹 가입 요청 리스트 조회는 리더만 가능
        if (httpMethod.equalsIgnoreCase("GET") && requestURI.matches("^/group/join/\\d+$")) {
            // groupId 추출
            Long groupId = (Long) Long.parseLong(requestURI.split("/")[3]);
            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            if (isNotGroupLeader(userEmail, groupId)) {
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

            if (isNotGroupLeader(userEmail, groupId)) {
                String message = "Access Denied: 그룹 리더만 관리할 수 있습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        /// 모집글 관련 요청
        // 모집글 작성은 그룹 리더만 가능
        if ((httpMethod.equalsIgnoreCase("POST") || httpMethod.equalsIgnoreCase("GET")) && requestURI.matches("^/recruitment/create$")) {

            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            if (!relationRepository.existsByMember_EmailAndGroupRole(userEmail, GroupRole.LEADER)) {
                String message = "Access Denied: 그룹장으로 가입된 그룹이 존재하지 않습니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 모집글 수정은 모집글 작성자만 가능
        if ((httpMethod.equalsIgnoreCase("PUT") || httpMethod.equalsIgnoreCase("DELETE")) && requestURI.matches("^/recruitment/\\d+$")) {

            // recruitmentId 추출
            Long recruitmentId = (Long) Long.parseLong(requestURI.split("/")[2]);

            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            Users loginUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

            Long authorId = recruitmentRepository.findAuthorIdByRecruitmentId(recruitmentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 존재하지 않습니다."));

            if (!loginUser.getUserId().equals(authorId)) {
                if (httpMethod.equalsIgnoreCase("PUT")) {
                    String message = "Access Denied: 작성자만 수정가능합니다!";
                    denyAccess(response, message);
                } else {
                    String message = "Access Denied: 작성자만 삭제가능합니다!";
                    denyAccess(response, message);
                }
                return; // 필터 체인 중단
            }
        }

        /// 댓글 관련 요청
        // 댓글 수정 및 삭제는 작성자만 가능
        if ((httpMethod.equalsIgnoreCase("PUT") || httpMethod.equalsIgnoreCase("DELETE")) && requestURI.matches("^/comment/\\d+/\\d+$")) {


            // commentId 추출
            Long commentId = (Long) Long.parseLong(requestURI.split("/")[3]);

            // 현재 인증된 사용자 정보 가져오기
            String userEmail = getAuthenticatedUserEmail();

            Users loginUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

            if (!loginUser.equals(comment.getAuthor())) {
                String message = "Access Denied: 작성자만 수정 및 삭제가 가능합니다!";
                denyAccess(response, message);
                return; // 필터 체인 중단
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("Authentication 객체가 null 입니다.");
            throw new IllegalStateException("사용자가 인증되지 않았습니다.");
        }
        return authentication.getName();
    }

    private boolean isNotGroupLeader(String userEmail, Long groupId) {
        RelationBetweenUserAndGroup relation = relationRepository.findByMember_EmailAndGroup_Id(userEmail, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 해당 그룹에 가입하지 않았습니다."));

        log.info("사용자의 그룹 역할: {}", relation.getGroupRole());
        log.info("{}", relation.getGroupRole() == GroupRole.LEADER);
        return relation.getGroupRole() != GroupRole.LEADER;
    }

    private void denyAccess(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(message);
    }
}
