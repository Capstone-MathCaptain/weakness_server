package MathCaptain.weakness.User.service;

import MathCaptain.weakness.User.dto.request.FindEmailRequestDto;
import MathCaptain.weakness.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.User.dto.response.FindEmailResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.User.dto.request.UpdateUserRequestDto;
import MathCaptain.weakness.User.dto.request.SaveUserRequestDto;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.User.dto.request.FindPwdRequestDto;
import MathCaptain.weakness.global.Mail.MailService;
import MathCaptain.weakness.global.exception.DuplicatedException;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    ConcurrentHashMap<String, String> emailMap = new ConcurrentHashMap<>();

    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));
    }

    public Users getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));
    }

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

    public ApiResponse<?> deleteUser(long userId, String password) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return ApiResponse.ok(null);
    }

    public ApiResponse<UserResponseDto> updateUser(Long userId ,UpdateUserRequestDto updateUser) {
        Users user = userRepository.findByEmail(updateUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        if (!user.getUserId().equals(userId)) {
            throw new IllegalArgumentException("유저가 일치하지 않습니다!");
        }

        if (!user.getName().equals(updateUser.getName()) && updateUser.getName() != null) {
            user.updateName(updateUser.getName());
        }

        if (!user.getNickname().equals(updateUser.getNickname()) && updateUser.getNickname() != null) {
            user.updateNickname(updateUser.getNickname());
        }

        if (!user.getPhoneNumber().equals(updateUser.getPhoneNumber()) && updateUser.getPhoneNumber() != null) {
            user.updatePhoneNumber(updateUser.getPhoneNumber());
        }

        return ApiResponse.ok(buildUserResponseDto(user));
    }

    public ApiResponse<UserResponseDto> getUserInfo(Long userId) {
        Users member = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        return ApiResponse.ok(buildUserResponseDto(member));
    }

    public ApiResponse<FindEmailResponseDto> findEmail(FindEmailRequestDto findEmailRequestDto) {

        Users user = userRepository.findByNameAndPhoneNumber(findEmailRequestDto.getUserName(), findEmailRequestDto.getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("이름과 전화번호를 다시 한 번 확인해주세요!"));

        return ApiResponse.ok(FindEmailResponseDto.builder()
                .email(user.getEmail())
                .build());
    }

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

    public void changePwd(ChangePwdDto changePwdDto) {
        String userEmail = emailMap.get(changePwdDto.getUuid());

        if (userEmail == null) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다.");
        }

        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 없습니다."));

        user.updatePassword(changePwdDto.getNewPassword() ,passwordEncoder);
    }


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

    //==빌더 생성==//

    private UserResponseDto buildUserResponseDto(Users user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
