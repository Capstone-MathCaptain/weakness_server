package MathCaptain.weakness.User.service;

import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.dto.request.SaveUserRequestDto;
import MathCaptain.weakness.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
class UserServiceTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    SaveUserRequestDto testUser = SaveUserRequestDto.builder()
            .email("testUser@email.com")
            .name("testUser001")
            .nickname("testUserNick001")
            .password("testUserPassword001")
            .phoneNumber("010-1234-5678")
            .build();

    @Test
    void getUserById() {
    }

    @Test
    void getUserByName() {
    }

    @Test
    void saveUser() {

        userService.saveUser(testUser);

        List<Users> users = userRepository.findAll();

        Assertions.assertThat(users.size()).isEqualTo(1);

    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUserInfo() {
    }
}