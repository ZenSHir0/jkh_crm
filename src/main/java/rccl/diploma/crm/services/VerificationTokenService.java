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
    private final UserRepository userRepository;

    private String createToken(User user, long active_hours) {
        verificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(active_hours))
                .createdAt(LocalDateTime.now())
                .build();

        verificationTokenRepository.save(verificationToken);
        user.getVerificationTokens().add(verificationToken);

        return token;
    }

    public String createVerificationToken(User user) {
        return createToken(user, 24);
    }

    public String createPasswordResetToken(User user) {
        return createToken(user, 1);
    }

    public User validatePasswordResetToken(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token).orElse(null);
        if (vt == null || vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            if (vt != null) verificationTokenRepository.delete(vt);
            return null;
        }
        return vt.getUser();
    }

    public User consumePasswordResetToken(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token).orElse(null);
        if (vt == null || vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            if (vt != null) verificationTokenRepository.delete(vt);
            return null;
        }
        User user = vt.getUser();
        verificationTokenRepository.delete(vt);
        return user;
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
