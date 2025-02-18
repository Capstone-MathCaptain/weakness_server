package MathCaptain.weakness.global.Mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public void sendChangePwdMail(String email, String UUID) throws MailException {
        // 메일 전송 로직
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("[의지박약] 비밀번호 재설정 링크");
        simpleMailMessage.setText("localhost:8080/user/reset/password?uuid=" + UUID + "\n" + "위 링크를 클릭하여 비밀번호를 재설정해주세요.");

        javaMailSender.send(simpleMailMessage);
    }




}
