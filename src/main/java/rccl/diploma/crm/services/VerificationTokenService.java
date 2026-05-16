package rccl.diploma.crm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.entity.VerificationToken;
import rccl.diploma.crm.repository.UserRepository;
import rccl.diploma.crm.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    private String createVerificationToken(User user) {

        verificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .createdAt(LocalDateTime.now())
                .build();

        verificationTokenRepository.save(verificationToken);
        user.getVerificationTokens().add(verificationToken);

        return token;
    }

    public void createAndSendVerificationToken(User user) {

        String token = createVerificationToken(user);

        emailService.sendVerificationEmail(user, token);
    }

    public boolean verifyToken(String token) {

        VerificationToken vt = verificationTokenRepository.findByToken(token)
                .orElse(null);

        if (vt == null || vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            if (vt != null) {
                verificationTokenRepository.delete(vt);
            }
            return false;
        }

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(vt);
        return true;
    }
}
