package MathCaptain.weakness.User.service;

import MathCaptain.weakness.Group.dto.response.UserResponseDto;
import MathCaptain.weakness.User.dto.updateUserDto;
import MathCaptain.weakness.User.dto.userDto;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.User.domain.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    }

    public Users getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    }

    // 회원가입
    public long saveUser(userDto user) {
        Users users = Users.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();
        validateDuplicateUser(users);
        return userRepository.save(users).getUserId();
    }

    public long deleteUser(long userId, String password) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user.getUserId();
    }

    public userDto getUserInfo(long userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        return userDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public Users updateUser(updateUserDto updateUser) {
        Users user = userRepository.findByEmail(updateUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        user.updateName(updateUser.getName());
        user.updateNickname(updateUser.getNickname());
        user.updatePhoneNumber(updateUser.getPhoneNumber());
        return user;
    }

    public UserResponseDto getUserInfo(Long userId) {
        Users member = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        return UserResponseDto.builder()
                .userId(member.getUserId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

    //==검증 로직==//
    private void validateDuplicateUser(Users user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalStateException("이미 사용중인 전화번호입니다.");
        }
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new IllegalStateException("이미 사용중인 닉네임입니다.");
        }
    }
}
