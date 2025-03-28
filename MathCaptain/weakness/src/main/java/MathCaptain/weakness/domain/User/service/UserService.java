package MathCaptain.weakness.domain.User.service;

import MathCaptain.weakness.domain.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.domain.Group.dto.response.UserGroupCardResponseDto;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Group.service.GroupService;
import MathCaptain.weakness.domain.User.dto.request.*;
import MathCaptain.weakness.domain.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.domain.User.dto.response.findEmailResponse;
import MathCaptain.weakness.domain.User.dto.response.UserCardResponse;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import MathCaptain.weakness.domain.User.repository.UserRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Mail.MailService;
import MathCaptain.weakness.global.exception.AuthorizationException;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final GroupService groupService;
    private final RelationRepository relationRepository;

    ConcurrentHashMap<String, String> emailMap = new ConcurrentHashMap<>();

    // 회원가입
    public ApiResponse<UserResponse> saveUser(SaveUserRequest userSaveRequest) {
        Users user = Users.of(userSaveRequest);
        userRepository.save(user);
        return ApiResponse.ok(UserResponse.of(user));
    }

    // 회원탈퇴
    public ApiResponse<?> deleteUser(Users user, UserDeleteRequest userDeleteRequest) {
        if (checkPassword(user, userDeleteRequest)) {
            throw new AuthorizationException("비밀번호가 일치하지 않습니다.");
        }
        userRepository.delete(user);
        return ApiResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 회원정보 수정
    public ApiResponse<UserResponse> updateUser(Users user, UpdateUserRequest userUpdateRequest) {
        user.updateUser(userUpdateRequest);
        List<Long> joinedGroupsId = relationRepository.findGroupsIdByMember(user);
        // TODO : 로직 수정 필요
        List<GroupResponseDto> groupResponseList = groupService.getUsersGroups(joinedGroupsId);
        return ApiResponse.ok(UserResponse.of(user, groupResponseList));
    }

    // 회원정보 조회
    public ApiResponse<UserResponse> getUserInfo(Long userId) {
        Users user = findByUserId(userId);
        List<Long> joinedGroupsId = relationRepository.findGroupsIdByUserId(user.getUserId());
        List<GroupResponseDto> groupResponseList = groupService.getUsersGroups(joinedGroupsId);
        return ApiResponse.ok(UserResponse.of(user, groupResponseList));
    }


    // 이메일 찾기
    public ApiResponse<findEmailResponse> findEmail(FindEmailRequest findEmailRequest) {
        Users user = findByNameAndPhoneNum(findEmailRequest);
        return ApiResponse.ok(findEmailResponse.of(user));
    }

    // 비밀번호 찾기 요청
    public void findPwdRequest(FindPwdRequest findPwdRequest) {
        if(!existsByEmailAndName(findPwdRequest)) {
            throw new IllegalArgumentException("이메일 또는 이름이 일치하지 않습니다.");
        }
        String UUID = java.util.UUID.randomUUID().toString();
        String email = findPwdRequest.getEmail();
        emailMap.put(UUID, email);
        mailService.sendChangePwdMail(email, UUID);
    }

    // 비밀번호 변경
    public void changePwd(ChangePwdDto changePwdDto) {
        String userEmail = emailMap.get(changePwdDto.getUuid());

        if (isNull(userEmail)) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다.");
        }
        Users user = findByEmail(userEmail);
        user.updatePassword(changePwdDto.getNewPassword() ,passwordEncoder);
    }

    public ApiResponse<UserCardResponse> getUserCard(Users user) {
        List<UserGroupCardResponseDto> groupCards = groupService.getUserGroupCard(user);
        UserCardResponse userCardResponse = UserCardResponse.of(user, groupCards);
        return ApiResponse.ok(userCardResponse);
    }

    /// 로직

    //==검증 로직==//

    private Users findByUserId(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));
    }

    private boolean checkPassword(Users user, UserDeleteRequest userDeleteRequest) {
        return !passwordEncoder.matches(userDeleteRequest.getPassword(), user.getPassword());
    }

    private boolean existsByEmailAndName(FindPwdRequest findPwdRequest) {
        return userRepository.existsByEmailAndName(findPwdRequest.getEmail(), findPwdRequest.getName());
    }

    private Users findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));
    }

    private boolean isNull(String userEmail) {
        return userEmail == null;
    }

    private Users findByNameAndPhoneNum(FindEmailRequest findEmailRequest) {
        return userRepository.findByNameAndPhoneNumber(findEmailRequest.getUserName(), findEmailRequest.getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("이름과 전화번호를 다시 한 번 확인해주세요!"));
    }
}
