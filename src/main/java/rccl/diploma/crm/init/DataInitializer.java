package rccl.diploma.crm.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rccl.diploma.crm.entity.enums.Role;
import rccl.diploma.crm.entity.User;
import rccl.diploma.crm.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("admin@admin.com")
                        .name("Максим")
                        .surname("Марцинкевич")
                        .lastName("Сергеевич")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
