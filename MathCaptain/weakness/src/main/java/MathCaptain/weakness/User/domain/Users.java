package MathCaptain.weakness.User.domain;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.User.dto.request.UpdateUserRequestDto;
import MathCaptain.weakness.User.enums.TierThresholds;
import MathCaptain.weakness.User.enums.Tiers;
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

    @OneToMany(mappedBy = "author")
    private List<Comment> comment;

    @OneToMany(mappedBy = "author")
    private List<Recruitment> recruitment;

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

    //== 수정 로직 ==//

    public void updateName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
        }
    }

    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.equals(this.nickname)) {
            this.nickname = nickname;
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.equals(this.phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateUser(UpdateUserRequestDto requestDto) {
        updateName(requestDto.getName());
        updateNickname(requestDto.getNickname());
        updatePhoneNumber(requestDto.getPhoneNumber());
    }

    public void updatePoint(Long point) {
        this.userPoint = point;
        evaluateTier();
    }

    public void addPoint(Long point) {
        this.userPoint += point;
        evaluateTier();
    }

    // 10% 감소
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
