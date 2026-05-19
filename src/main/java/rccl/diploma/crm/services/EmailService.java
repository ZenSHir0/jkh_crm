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
        message.setText("Здравствуйте, " + user.getFullName() + "!\n\n" +
                "Чтобы активировать аккаунт, перейдите по ссылке: http://localhost:8080/verify?token=" + token + "\n\n" +
                "Ссылка действительна 24 часа.");
        mailSender.send(message);
    }

    public void sendPasswordReset(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(System.getenv("GMAIL_USERNAME"));
        message.setTo(user.getEmail());
        message.setSubject("Сброс пароля");
        message.setText("Здравствуйте, " + user.getFullName() + "! Кто-то запросил ссылку для изменения пароля вашего аккаунта в ЖКХ.CRM, если это были не вы просто проигнорируйте данное сообщение.\n\n" +
                "Для сброса пароля перейдите по ссылке: http://localhost:8080/reset-password?token=" + token + "\n\n" +
                "Ссылка действительна 1 час.");
        mailSender.send(message);
    }
}
