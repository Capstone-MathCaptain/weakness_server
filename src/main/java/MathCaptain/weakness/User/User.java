package MathCaptain.weakness.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Range(min = 7, max = 15)
    private String password;

    @Range(min = 3, max = 15)
    @NotNull
    private String name;

    @Range(min = 3, max = 15)
    @NotNull
    private String nickname;

    @Email
    private String email;

    @Range(min = 11, max = 11)
    private String phone_number;
}
