package MathCaptain.weakness.domain.User.entity;

import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.User.dto.request.SaveUserRequest;
import MathCaptain.weakness.domain.User.dto.request.UpdateUserRequest;
import MathCaptain.weakness.domain.User.enums.TierThresholds;
import MathCaptain.weakness.domain.User.enums.Tiers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Range(min = 0)
    private Long userPoint;

    @OneToMany(mappedBy = "member")
    private List<RelationBetweenUserAndGroup> relationBetweenUserAndGroup;

    @OneToMany(mappedBy = "author")
    private List<Comment> comment;

    @OneToMany(mappedBy = "author")
    private List<Recruitment> recruitment;

    @Enumerated(value = EnumType.STRING)
    private Tiers tier;

    //== jwt 토큰 추가 ==//
    @Column(length = 1000)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    @PrePersist
    protected void onCreate() {
        this.userPoint = 0L;
        this.tier = Tiers.BRONZE;
    }

    private Users(String email, String password, String name, String nickname, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    public static Users of(SaveUserRequest userRequestDto) {
        return new Users(
                userRequestDto.getEmail(),
                userRequestDto.getPassword(),
                userRequestDto.getName(),
                userRequestDto.getNickname(),
                userRequestDto.getPhoneNumber());
    }

    //== 수정 로직 ==//
    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateUser(UpdateUserRequest requestDto) {
        this.nickname = requestDto.getNickname();
        this.name = requestDto.getName();
        this.phoneNumber = requestDto.getPhoneNumber();
    }

    public void updatePoint(Long point) {
        this.userPoint = point;
        evaluateTier();
    }

    public void addPoint(Long point) {
        this.userPoint += point;
        evaluateTier();
    }

    public void subtractPoint(Long point) {
        this.userPoint = Math.max(0, this.userPoint - point);
        evaluateTier();
    }

    //== 티어 평가 로직 ==//
    private void evaluateTier() {
        if (this.userPoint >= TierThresholds.MASTER) {
            this.tier = Tiers.MASTER;
        } else if (this.userPoint >= TierThresholds.DIAMOND) {
            this.tier = Tiers.DIAMOND;
        } else if (this.userPoint >= TierThresholds.PLATINUM) {
            this.tier = Tiers.PLATINUM;
        } else if (this.userPoint >= TierThresholds.GOLD) {
            this.tier = Tiers.GOLD;
        } else if (this.userPoint >= TierThresholds.SILVER) {
            this.tier = Tiers.SILVER;
        } else {
            this.tier = Tiers.BRONZE;
        }
    }
}
