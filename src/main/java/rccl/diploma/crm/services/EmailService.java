package rccl.diploma.crm.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import rccl.diploma.crm.entity.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(System.getenv("GMAIL_USERNAME"));
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение почты в системе CRM");
        message.setText("Ку, " + user.getFullName() + "!\n\n" +
                "Чтобы активировать аккаунт, перейди по ссылке: http://localhost:8080/verify?token=" + token + "\n\n" +
                "Ссылка действительна 24 часа.");
        mailSender.send(message);
    }
}
