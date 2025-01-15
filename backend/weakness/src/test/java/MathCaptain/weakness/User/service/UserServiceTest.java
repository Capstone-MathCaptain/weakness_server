package MathCaptain.weakness.User.service;

import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.dto.request.SaveUserRequestDto;
import MathCaptain.weakness.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private final UserService userService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final PasswordEncoder passwordEncoder;

    SaveUserRequestDto testUser = SaveUserRequestDto.builder()
            .email("testUser@email.com")
            .name("testUser001")
            .nickname("testUserNick001")
            .password("testUserPassword001")
            .phoneNumber("010-1234-5678")
            .build();

    UserServiceTest(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserByName() {
    }

    @Test
    void saveUser() {
        // Mock the password encoding
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");

        // Mock the repository save method
        Users savedUser = Users.builder()
                .email(testUser.getEmail())
                .password("encodedPassword")
                .name(testUser.getName())
                .nickname(testUser.getNickname())
                .phoneNumber(testUser.getPhoneNumber())
                .build();
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);

        userService.saveUser(testUser);

        // Verify the save method was called
        verify(userRepository, times(1)).save(any(Users.class));

        // Verify the user properties
        List<Users> users = userRepository.findAll();
        Assertions.assertThat(users.size()).isEqualTo(1);
        Assertions.assertThat(users.get(0).getEmail()).isEqualTo(testUser.getEmail());
        Assertions.assertThat(users.get(0).getPassword()).isEqualTo("encodedPassword");
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