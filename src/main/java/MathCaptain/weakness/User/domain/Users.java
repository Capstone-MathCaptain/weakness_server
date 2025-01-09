package MathCaptain.weakness.User.domain;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Email
    @NotEmpty(message = "이메일은 필수입니다!")
    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @NotNull(message = "비밀번호는 필수입니다!")
    private String password;

    @NotNull(message = "이름은 필수입니다!")
    @Size(min = 3, max = 15, message = "이름은 최소 3글자 이상, 15글자 이하입니다.")
    private String name;

    @Size(min = 3, max = 15, message = "별명은 최소 3글자 ~ 15글자 이하입니다.")
    @NotNull(message = "별명을 지정해주세요!")
    private String nickname;

    @Size(min = 11, max = 13, message = "전화번호를 잘못입력하셨습니다! 다시 입력해주세요.")
    private String phoneNumber;

    @OneToOne(mappedBy = "member")
    private RelationBetweenUserAndGroup relationBetweenUserAndGroup;

    @OneToMany(mappedBy = "leader")
    private List<Group> group;

    //== jwt 토큰 추가 ==//
    @Column(length = 1000)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getPassword());
    }

    //== 수정 로직 ==//

    public void updateName(String name) {
        this.name = name;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
