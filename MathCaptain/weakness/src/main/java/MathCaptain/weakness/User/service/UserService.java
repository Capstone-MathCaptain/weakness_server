package MathCaptain.weakness.User.service;

import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.UserGroupCardResponseDto;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Group.service.RelationService;
import MathCaptain.weakness.User.dto.request.*;
import MathCaptain.weakness.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.User.dto.response.FindEmailResponseDto;
import MathCaptain.weakness.User.dto.response.UserCardResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Mail.MailService;
import MathCaptain.weakness.global.exception.DuplicatedException;
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
    public ApiResponse<UserResponseDto> saveUser(SaveUserRequestDto user) {
        Users users = Users.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();

        validateDuplicateUser(users);
        userRepository.save(users);

        return ApiResponse.ok(buildUserResponseDto(users));
    }

    // 회원탈퇴
    public ApiResponse<?> deleteUser(Users user, UserDeleteRequestDto userDeleteRequestDto) {

        if (!passwordEncoder.matches(userDeleteRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);

        return ApiResponse.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 회원정보 수정
    public ApiResponse<UserResponseDto> updateUser(Users user, UpdateUserRequestDto updateUser) {

        if (!user.getName().equals(updateUser.getName()) && updateUser.getName() != null) {
            user.updateName(updateUser.getName());
        }

        if (!user.getNickname().equals(updateUser.getNickname()) && updateUser.getNickname() != null) {
            user.updateNickname(updateUser.getNickname());
        }

        if (!user.getPhoneNumber().equals(updateUser.getPhoneNumber()) && updateUser.getPhoneNumber() != null) {
            user.updatePhoneNumber(updateUser.getPhoneNumber());
        }

        List<Long> joinedGroupsId = relationRepository.findGroupsIdByMember(user);
        List<GroupResponseDto> groupResponseDtoList = groupService.getUsersGroups(joinedGroupsId);

        return ApiResponse.ok(buildUserResponseDto(user, groupResponseDtoList));
    }

    // 회원정보 조회
    public ApiResponse<UserResponseDto> getUserInfo(Long userId) {
        Users member = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        List<Long> joinedGroupsId = relationRepository.findGroupsIdByUserId(member.getUserId());
        List<GroupResponseDto> groupResponseDtoList = groupService.getUsersGroups(joinedGroupsId);

        return ApiResponse.ok(buildUserResponseDto(member, groupResponseDtoList));
    }

    // 이메일 찾기
    public ApiResponse<FindEmailResponseDto> findEmail(FindEmailRequestDto findEmailRequestDto) {

        Users user = userRepository.findByNameAndPhoneNumber(findEmailRequestDto.getUserName(), findEmailRequestDto.getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("이름과 전화번호를 다시 한 번 확인해주세요!"));

        return ApiResponse.ok(FindEmailResponseDto.builder()
                .email(user.getEmail())
                .build());
    }

    // 비밀번호 찾기 요청
    public void findPwdRequest(FindPwdRequestDto findPwdRequestDto) {
        String email = findPwdRequestDto.getEmail();
        String name = findPwdRequestDto.getName();

        if(!checkUserByEmailAndName(email, name)){
            throw new IllegalArgumentException("이메일 또는 이름이 일치하지 않습니다.");
        }

        String UUID = java.util.UUID.randomUUID().toString();
        emailMap.put(UUID, email);

        mailService.sendChangePwdMail(email, UUID);
    }

    // 비밀번호 변경
    public void changePwd(ChangePwdDto changePwdDto) {
        String userEmail = emailMap.get(changePwdDto.getUuid());

        if (userEmail == null) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다.");
        }

        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        user.updatePassword(changePwdDto.getNewPassword() ,passwordEncoder);
    }

    public ApiResponse<UserCardResponseDto> getUserCard(Users user) {

        List<UserGroupCardResponseDto> groupCards = groupService.getUserGroupCard(user);

        return ApiResponse.ok(buildUserCardResponseDto(user, groupCards));
    }

    /// 로직

    //==검증 로직==//
    private void validateDuplicateUser(Users user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicatedException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new DuplicatedException("이미 사용중인 전화번호입니다.");
        }
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new DuplicatedException("이미 사용중인 닉네임입니다.");
        }
    }

    public boolean checkUserByEmailAndName(String email, String username) {
        return userRepository.existsByEmailAndName(email, username);
    }

    /// 빌더

    private UserResponseDto buildUserResponseDto(Users user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .tier(user.getTier())
                .build();
    }

    private UserResponseDto buildUserResponseDto(Users user, List<GroupResponseDto> joinedGroups) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .joinedGroups(joinedGroups)
                .tier(user.getTier())
                .build();
    }

    private UserCardResponseDto buildUserCardResponseDto(Users user, List<UserGroupCardResponseDto> groupCards) {
        return UserCardResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .userTier(user.getTier())
                .userPoint(user.getUserPoint())
                .groupCards(groupCards)
                .build();
    }
}
