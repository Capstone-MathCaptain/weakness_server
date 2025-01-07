package MathCaptain.weakness.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void saveUser(Users user) {
        validateDuplicateUser(user);
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

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
