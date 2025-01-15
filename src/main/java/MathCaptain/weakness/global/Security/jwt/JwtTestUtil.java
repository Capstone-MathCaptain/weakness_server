package MathCaptain.weakness.global.Security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtTestUtil {

    private static final String SECRET = "your_secret_key";
    private static final String USERNAME_CLAIM = "email";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public static String createTestJwt(String email) {
        return JWT.create()
                .withSubject("Test")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim(USERNAME_CLAIM, email)
                .sign(Algorithm.HMAC512(SECRET));

    }

}