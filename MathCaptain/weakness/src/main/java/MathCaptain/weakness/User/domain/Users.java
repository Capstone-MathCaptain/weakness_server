package MathCaptain.weakness.User.domain;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phoneNumber;

    @Range(min = 0)
    private Long userPoint;

    @OneToMany(mappedBy = "member")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @OneToMany(mappedBy = "leader")
    private List<Group> group;

    @OneToMany(mappedBy = "author")
    private List<Comment> comment;

    @OneToMany(mappedBy = "author")
    private List<Recruitment> recruitment;

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

    @PrePersist
    protected void onCreate() {
        this.userPoint = 0L;
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

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

}
