package MathCaptain.weakness.domain.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "USERS")
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Email
    @NotEmpty(message = "이메일은 필수입니다!")
    @Column(nullable = false, unique = true, length = 30)
    private String email;

//    @NotNull(message = "아이디는 필수입니다!")
//    @Range(min = 5, max = 15, message = "아이디는 최소 5글자 이상, 15글자 이하입니다.")
//    private String loginId;

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

}
